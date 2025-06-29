package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.command.ICommandAlias.ListedAlias
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.CommonEncrypt
import cc.sukazyo.cono.morny.util.CommonEncrypt.*
import cc.sukazyo.cono.morny.util.ConvertByteHex.toHex
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
import com.pengrad.telegrambot.model.{PhotoSize, Update}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{GetFile, SendDocument, SendMessage, SendSticker}

import java.io.IOException
import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.{Charset, IllegalCharsetNameException, UnsupportedCharsetException}
import java.util.Base64
import scala.language.postfixOps
import scala.util.boundary
import scala.util.boundary.break

/** Provides Telegram Command __`/encrypt`__. */
class Encryptor (using coeur: MornyCoeur) extends ITelegramCommand {
	
	override val name: String = "encrypt"
	override val aliases: Array[ICommandAlias] | Null = Array(ListedAlias("enc"))
	override val paramRule: String = "[algorithm|(l)] [(uppercase)]"
	override val description: String = "通过指定算法加密回复的内容 (目前只支持文本)"
	
	override def execute (using command: InputCommand, event: Update): Unit = boundary {
		
		val args = command.args
		
		// show a simple help page
		if ((args isEmpty) || ((args(0) equals "l") && (args.length == 1)))
			echoHelp(event.message.chat.id, event.message.messageId)
			return
		
		def _is_mod_u(arg: String): Boolean =
			if (arg equalsIgnoreCase "uppercase") return true
			if (arg equalsIgnoreCase "u") return true
			if (arg equalsIgnoreCase "upper") return true
			false
		
		/** The param entity of command.
		  *
		  * Each entity has its text value, and a state about if it has used.
		  */
		case class EXParam (text: String) {
			var _isUsed = false
			def used (): Unit = _isUsed = true
			def isUsed: Boolean = _isUsed
		}
		/** List of [[EXParam]]. contains all params after the encryption algorithm. */
		implicit val params: List[EXParam] =
			args.toList.drop(1).map(EXParam(_))
		
		// test if uppercase param is set, also set the uppercase params used.
		lazy val mod_uppercase = params.exists { param =>
			if _is_mod_u(param.text) then
				param.used()
				true
			else false
		}
		
		lazy val mod_charset: Charset =
			params.find(_.text.startsWith("-e")) match
				case Some(param) =>
					param.used()
					val charsetName = param.text.drop("-e".length)
					try Charset.forName(charsetName)
					catch case _: (IllegalCharsetNameException | UnsupportedCharsetException) =>
						coeur.account exec SendMessage(
							event.message.chat.id,
							// language=html
							s"<b>Unsupported Charset:</b> <code>${h(charsetName)}</code>".stripMargin
						).replyToMessageId(event.message.messageId)
						break()
				case None => CommonEncrypt.ENCRYPT_STANDARD_CHARSET
//		lazy val mod_enc_charset = modCharsetFind("-ee")
//		lazy val mod_dec_charset = modCharsetFind("-ed")
		
		// BLOCK: get input
		// for now, only support getting data from replied message, and
		// this message CAN ONLY have texts or an universal file: if the
		// universal files are not only one, only the first one can be get.
		// - do NOT SUPPORT telegram inline image/video/autio yet
		// - do NOT SUPPORT multi-file yet
		// todo: support inline image/video/audio file and multi-files.
		/** inner trait: the encryptable data abstract */
		trait XEncryptable { /** standards data to [[Array]]`[`[[Byte]]`]` for processing */ val asByteArray: Array[Byte] }
		/** inner class: the [[XEncryptable]] implementation of binary([[Array]]`[`[[Byte]]`]`) data (file or something) */
		case class XFile (data: Array[Byte], name: String) extends XEncryptable:
			val asByteArray: Array[Byte] = data
		/** inner class: the [[XEncryptable]] implementation of [[String]] data */
		case class XText (data: String) extends XEncryptable:
			val str: String = data
			val asByteArray: Array[Byte] = data.getBytes(mod_charset)
		val input: XEncryptable =
			val _r = event.message.replyToMessage
			if ((_r ne null) && (_r.document ne null)) {
				try {XFile(
					coeur.account getFileContent (coeur.account exec GetFile(_r.document.fileId)).file,
					_r.document.fileName
				)} catch case e: IOException =>
					logger warn s"NetworkRequest error: TelegramFileAPI:\n\t${e.getMessage}"
					coeur.daemons.reporter.exception(e, "NetworkRequest error: TelegramFileAPI")
					return
			} else if ((_r ne null) && (_r.photo ne null)) {
				try {
					var _photo_origin: PhotoSize = null
					var _photo_size: Long = 0
					for (size <- _r.photo)
						val _size = (size.width longValue)*size.height
						if (_photo_size < _size)
							_photo_origin = size
							_photo_size = _size
					if (_photo_origin eq null) throw IllegalArgumentException("no photo from api.")
					import cc.sukazyo.cono.morny.util.UseRandom.rand_id
					XFile(
						coeur.account getFileContent (coeur.account exec GetFile(_photo_origin.fileId)).file,
						s"photo$rand_id.png"
					)
				} catch
					case e: IOException =>
						//noinspection DuplicatedCode
						logger warn s"NetworkRequest error: TelegramFileAPI:\n\t${e.getMessage}"
						coeur.daemons.reporter.exception(e, "NetworkRequest error: TelegramFileAPI")
						return
					case e: IllegalArgumentException =>
						logger warn s"FileProcess error: PhotoSize:\n\t${e.getMessage}"
						coeur.daemons.reporter.exception(e, "FileProcess error: PhotoSize")
						return
			} else if ((_r ne null) && (_r.text ne null)) {
				XText(_r.text)
			} else {
				coeur.account exec SendMessage(
					event.message.chat.id,
					"<i><u>null</u></i>"
				).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
				return
			}
		// END BLOCK: get input
		
		// BLOCK: encrypt
		/** inner class: encrypt result implementation of text-like (can be described as [[String]]). */
		trait EXTextLike { val text: String }
		/** inner class: encrypt result implementation of a file */
		case class EXFile (result: Array[Byte], resultName: String)
		/** inner class: [[EXTextLike]] implementation of just normal text */
		case class EXText (text: String) extends EXTextLike
		/** inner class: [[EXTextLike]] implementation of a special type: hash value */
		case class EXHash (text: String) extends EXTextLike
		/** generate encrypt result by making normal encrypt: output type == input type */
		def genResult_encrypt (
			source: XEncryptable, processor: Array[Byte]=>Array[Byte], filenameProcessor: String=>String
		): EXFile|EXText = {
			source match
				case x_file: XFile => EXFile(processor(x_file asByteArray), filenameProcessor(x_file.name))
				case x: XText =>
					EXText(String(processor(x asByteArray), mod_charset))
		}
		/** generate encrypt result by making hash: output type == hash value */
		def genResult_hash (source: XEncryptable, processor: Array[Byte]=>Array[Byte]): EXHash =
			val hashed = processor(source asByteArray) toHex;
			EXHash(if mod_uppercase then hashed toUpperCase else hashed)
		//noinspection UnitMethodIsParameterless
		def echo_unsupported: Unit =
			coeur.account exec SendSticker(
				event.message.chat.id,
				TelegramStickers ID_404
			).replyToMessageId(event.message.messageId)
		val result: EXHash|EXFile|EXText = args(0) match
			case "base64" | "base" | "baseu" | "b64" | "base64url" | "base64u" | "b64u" =>
				val _tool_b64 =
					if args(0) contains "u" then Base64.getUrlEncoder
					else Base64.getEncoder
				genResult_encrypt(
					input,
					_tool_b64.encode,
					n => n+".b64.txt"
				)
			case "base64decode"  | "based" | "baseud" | "base64d" | "b64d" | "base64url-decode" | "base64ud" | "b64ud" =>
				val _tool_b64d =
					if args(0) contains "u" then Base64.getUrlDecoder
					else Base64.getDecoder
				try { genResult_encrypt(
					input,
					_tool_b64d.decode,
					CommonEncrypt.lint_base64FileName
				) } catch case _: IllegalArgumentException =>
					echo_unsupported
					return
			case "urlencoder" | "urlencode" | "urlenc" | "url" =>
				input match
					case x: XText =>
						EXText(URLEncoder.encode(x.data, mod_charset))
					case _: XFile => echo_unsupported; return;
			case "urldecoder" | "urldecode" | "urldec" | "urld" =>
				input match
					case _: XFile => echo_unsupported; return;
					case x: XText =>
						try { EXText(URLDecoder.decode(x.data, mod_charset)) }
						catch case _: IllegalArgumentException =>
								echo_unsupported
								return
			case "md5" => genResult_hash(input, MD5)
			case "sha1" => genResult_hash(input, SHA1)
			case "sha256" => genResult_hash(input, SHA256)
			case "sha512" => genResult_hash(input, SHA512)
			case _ =>
				echo_unsupported; return;
		// END BLOCK: encrypt
		
		// warning if params not used:
		if params.exists(_.isUsed == false) then
			coeur.account exec SendMessage(
				event.message.chat.id,
				// language=html
				s"""<b>Unknown Params:</b>
				   |${params.filterNot(_.isUsed).map(x => s" - <code>${h(x.text)}</code>").mkString("\n")}""".stripMargin
			).replyToMessageId(event.message.messageId).parseMode(ParseMode.HTML)
			return
		
		// output
		result match
			case _file: EXFile =>
				coeur.account exec SendDocument(
					event.message.chat.id,
					_file.result
				).fileName(_file.resultName).replyToMessageId(event.message.messageId)
			case _text: EXTextLike =>
				import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
				coeur.account exec SendMessage(
					event.message.chat.id,
					// language=html
					s"<pre><code>${h(_text.text)}</code></pre>"
				).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
		
	}
	
	/** echo help to a specific message in a specific chat.
	  *
	  * === the help message ===
	  * The first paragraph lists available encrypt algorithms and its alias,
	  * each line have one algorithm where the first name highlighted is the
	  * main name and following is aliases separated with `,`.
	  * with the separator `---`, the second paragraph lists available mods
	  * for algorithms, displays with the same rule of algorithms, with an extra
	  * italic text following describes its usage environment.
	  *
	  * when output to telegram just like:
	  * <blockquote>
	  * 	'''base64''', base, b64<br>
	  * 	'''base64u''', baseu, base64u, b64u<br>
	  * 	'''base64decode''', based, base64d, b64d<br>
	  * 	'''base64url-decode''', baseud, base64ud, b64ud<br>
	  *     '''urlencoder''', urlencode, urlenc, url<br>
	  *     '''urldecoder''', urldecode, urldec, urld<br>
	  * 	'''sha1'''<br>
	  * 	'''sha256'''<br>
	  * 	'''sha512'''<br>
	  * 	'''md5'''<br>
	  * 	---<br>
	  * 	'''uppercase''', upper, u ''(sha1/sha256/sha512/md5 only)''
	  * 	'''-e__{charset_name}__''' ''(base64/url encode/decode only)''
	  * </blockquote>
	  */
	private def echoHelp(chat: Long, replyTo: Int): Unit =
		coeur.account exec SendMessage(
			chat,
			// language=html
			s"""<b>base64</b>, base, b64
			   |<b>base64url</b>, baseu, base64u, b64u
			   |<b>base64decode</b>, based, base64d, b64d
			   |<b>base64url-decode</b>, baseud, base64ud, b64ud
			   |<b>urlencoder</b>, urlencode, urlenc, url
			   |<b>urldecoder</b>, urldecode, urldec, urld
			   |<b>sha1</b>
			   |<b>sha256</b>
			   |<b>sha512</b>
			   |<b>md5</b>
			   |---
			   |<b><i>uppercase</i></b>, upper, u <i>(sha1/sha256/sha512/md5 only)</i>
			   |<b><i>-e<u>&lt;charset_name&gt;</u></i></b> <i>(base64 encode/decode only)</i>
			   |<b><i>file</i></b>, f <i>(base64 encode/decode only)</i>"""
			.stripMargin
		).replyToMessageId(replyTo).parseMode(ParseMode HTML)
	
}
