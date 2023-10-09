package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.CommonEncrypt
import cc.sukazyo.cono.morny.util.CommonEncrypt.*
import cc.sukazyo.cono.morny.util.ConvertByteHex.toHex
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.{PhotoSize, Update}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{GetFile, SendDocument, SendMessage, SendSticker}

import java.io.IOException
import java.util.Base64
import scala.language.postfixOps

/** Provides Telegram Command __`/encrypt`__. */
class Encryptor (using coeur: MornyCoeur) extends ITelegramCommand {
	
	override val name: String = "encrypt"
	override val aliases: Array[ICommandAlias] | Null = null
	override val paramRule: String = "[algorithm|(l)] [(uppercase)]"
	override val description: String = "通过指定算法加密回复的内容 (目前只支持文本)"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		val args = command.args
		
		// show a simple help page
		if ((args isEmpty) || ((args(0) equals "l") && (args.length == 1)))
			echoHelp(event.message.chat.id, event.message.messageId)
			return
		
		// for mod-params:
		// mod-params is the args belongs to the encrypt algorithm.
		// due to the algorithm is defined in the 1st (array(0)) arg,
		// so the mod-params is which defined since the 2nd arg. also
		// due to there's only one mod-param yet (it is uppercase),
        // so the algorithm will be and must be in the 2nd arg.
		/** inner function: is input `arg` means mod-param ''uppercase'' */
		def _is_mod_u(arg: String): Boolean =
			if (arg equalsIgnoreCase "uppercase") return true
			if (arg equalsIgnoreCase "u") return true
			if (arg equalsIgnoreCase "upper") return true
			false
		val mod_uppercase = if (args.length > 1) {
			if (args.length < 3 && _is_mod_u(args(1))) true
			else
				coeur.account exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_404
				).replyToMessageId(event.message.messageId)
				return
		} else false
		
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
			 val asByteArray: Array[Byte] = data getBytes CommonEncrypt.ENCRYPT_STANDARD_CHARSET
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
		def genResult_encrypt (source: XEncryptable, processor: Array[Byte]=>Array[Byte], filenameProcessor: String=>String): EXFile|EXText = {
			source match
				case x_file: XFile => EXFile(processor(x_file asByteArray), filenameProcessor(x_file.name))
				case x: XText => EXText(String(processor(x asByteArray), CommonEncrypt.ENCRYPT_STANDARD_CHARSET))
		}
		/** generate encrypt result by making hash: output type == hash value */
		def genResult_hash (source: XEncryptable, processor: Array[Byte]=>Array[Byte]): EXHash =
			val hashed = processor(source asByteArray) toHex;
			EXHash(if mod_uppercase then hashed toUpperCase else hashed)
		val result: EXHash|EXFile|EXText = args(0) match
			case "base64" | "b64" | "base64url" | "base64u" | "b64u" =>
				val _tool_b64 =
					if args(0) contains "u" then Base64.getUrlEncoder
					else Base64.getEncoder
				genResult_encrypt(
					input,
					_tool_b64.encode,
					n => n+".b64.txt"
				)
			case "base64decode" | "base64d" | "b64d" | "base64url-decode" | "base64ud" | "b64ud" =>
				val _tool_b64d =
					if args(0) contains "u" then Base64.getUrlDecoder
					else Base64.getDecoder
				try { genResult_encrypt(
					input,
					_tool_b64d.decode,
					CommonEncrypt.lint_base64FileName
				) } catch case _: IllegalArgumentException =>
					coeur.account exec SendSticker(
						event.message.chat.id,
						TelegramStickers ID_404 // todo: is here better erro notify?
					).replyToMessageId(event.message.messageId)
					return
			case "md5" => genResult_hash(input, MD5)
			case "sha1" => genResult_hash(input, SHA1)
			case "sha256" => genResult_hash(input, SHA256)
			case "sha512" => genResult_hash(input, SHA512)
			case _ =>
				coeur.account exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_404
				).replyToMessageId(event.message.messageId)
				return;
		// END BLOCK: encrypt
		
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
	  * 	'''__base64__''', b64<br>
	  * 	'''__base64url__''', base64u, b64u<br>
	  * 	'''__base64decode__''', base64d, b64d<br>
	  * 	'''__base64url-decode__''', base64ud, b64ud<br>
	  * 	'''__sha1__'''<br>
	  * 	'''__sha256__'''<br>
	  * 	'''__sha512__'''<br>
	  * 	'''__md5__'''<br>
	  * 	---<br>
	  * 	'''__uppercase__''', upper, u ''(sha1/sha256/sha512/md5 only)''
	  * </blockquote>
	  */
	private def echoHelp(chat: Long, replyTo: Int): Unit =
		coeur.account exec SendMessage(
			chat,
			s"""<b><u>base64</u></b>, b64
			   |<b><u>base64url</u></b>, base64u, b64u
			   |<b><u>base64decode</u></b>, base64d, b64d
			   |<b><u>base64url-decode</u></b>, base64ud, b64ud
			   |<b><u>sha1</u></b>
			   |<b><u>sha256</u></b>
			   |<b><u>sha512</u></b>
			   |<b><u>md5</u></b>
			   |---
			   |<b><i>uppercase</i></b>, upper, u <i>(sha1/sha256/sha512/md5 only)</i>"""
			.stripMargin
		).replyToMessageId(replyTo).parseMode(ParseMode HTML)
	
}
