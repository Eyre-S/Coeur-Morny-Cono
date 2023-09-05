package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.daemon.MornyReport
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.CommonConvert.byteArrayToHex
import cc.sukazyo.cono.morny.util.CommonEncrypt
import cc.sukazyo.cono.morny.util.CommonEncrypt.*
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.{PhotoSize, Update}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{GetFile, SendDocument, SendMessage, SendSticker}

import java.io.IOException
import java.util.Base64
import scala.language.postfixOps

object Encryptor extends ITelegramCommand {
	
	override def getName: String = "encrypt"
	override def getAliases: Array[String] = null
	override def getParamRule: String = "[algorithm|(l)] [(uppercase)]"
	override def getDescription: String = "通过指定算法加密回复的内容 (目前只支持文本)"
	
	override def execute (command: InputCommand, event: Update): Unit = {
		
		val args = command.getArgs
		
		if ((args isEmpty) || ((args(0) equals "l") && (args.length == 1)))
			echoHelp(event.message.chat.id, event.message.messageId)
			return
		
		def _is_mod_u(arg: String): Boolean =
			if (arg equalsIgnoreCase "uppercase") return true
			if (arg equalsIgnoreCase "u") return true
			if (arg equalsIgnoreCase "upper") return true
			false
		val mod_uppercase = if (args.length > 1) {
			if (args.length < 3 && _is_mod_u(args(1))) true
			else
				MornyCoeur.extra exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_404
				).replyToMessageId(event.message.messageId)
				return
		} else false
		
		trait XEncryptable { val asByteArray: Array[Byte] }
		case class XFile (data: Array[Byte], name: String) extends XEncryptable {
			val asByteArray: Array[Byte] = data
		}
		case class XText (data: String) extends XEncryptable {
			 val asByteArray: Array[Byte] = data getBytes CommonEncrypt.ENCRYPT_STANDARD_CHARSET
		}
		val input: XEncryptable =
			val _r = event.message.replyToMessage
			if ((_r ne null) && (_r.document ne null)) {
				try {XFile(
					MornyCoeur.getAccount getFileContent (MornyCoeur.extra exec GetFile(_r.document.fileId)).file,
					_r.document.fileName
				)} catch case e: IOException =>
					logger warn s"NetworkRequest error: TelegramFileAPI:\n\t${e.getMessage}"
					MornyReport.exception(e, "NetworkRequest error: TelegramFileAPI")
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
					XFile(
						MornyCoeur.getAccount getFileContent (MornyCoeur.extra exec GetFile(_photo_origin.fileId)).file,
						s"photo${byteArrayToHex(hashMd5(System.currentTimeMillis toString)) substring 32-12 toUpperCase}.png"
					)
				} catch
					case e: IOException =>
						logger warn s"NetworkRequest error: TelegramFileAPI:\n\t${e.getMessage}"
						MornyReport.exception(e, "NetworkRequest error: TelegramFileAPI")
						return
					case e: IllegalArgumentException =>
						logger warn s"FileProcess error: PhotoSize:\n\t${e.getMessage}"
						MornyReport.exception(e, "FileProcess error: PhotoSize")
						return
			} else if ((_r ne null) && (_r.text ne null)) {
				XText(_r.text)
			} else {
				MornyCoeur.extra exec SendMessage(
					event.message.chat.id,
					"<i><u>null</u></i>"
				).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
				return
			}
		
		
		trait EXTextLike { val text: String }
		case class EXFile (result: Array[Byte], resultName: String)
		case class EXText (result: String) extends EXTextLike  { override val text:String = result }
		case class EXHash (result: String) extends EXTextLike  { override val text:String = result }
		def genResult_encrypt (source: XEncryptable, processor: Array[Byte]=>Array[Byte], filenameProcessor: String=>String): EXFile|EXText = {
			source match
				case x_file: XFile => EXFile(processor(x_file asByteArray), filenameProcessor(x_file.name))
				case x: XText => EXText(String(processor(x asByteArray), ENCRYPT_STANDARD_CHARSET))
		}
		def genResult_hash (source: XEncryptable, processor: Array[Byte]=>Array[Byte]): EXHash =
			val hashed = byteArrayToHex(processor(source asByteArray))
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
					CommonEncrypt.base64FilenameLint
				) } catch case _: IllegalArgumentException =>
					MornyCoeur.extra exec SendSticker(
						event.message.chat.id,
						TelegramStickers ID_404 // todo: is here better erro notify?
					).replyToMessageId(event.message.messageId)
					return
			case "md5" => genResult_hash(input, hashMd5)
			case "sha1" => genResult_hash(input, hashSha1)
			case "sha256" => genResult_hash(input, hashSha256)
			case "sha512" => genResult_hash(input, hashSha512)
			case _ =>
				MornyCoeur.extra exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_404
				).replyToMessageId(event.message.messageId)
				return;
		
		result match
			case _file: EXFile =>
				MornyCoeur.extra exec SendDocument(
					event.message.chat.id,
					_file.result
				).fileName(_file.resultName).replyToMessageId(event.message.messageId)
			case _text: EXTextLike =>
				import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml as h
				MornyCoeur.extra exec SendMessage(
					event.message.chat.id,
					s"<pre><code>${h(_text.text)}</code></pre>"
				).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
		
	}
	
	private def echoHelp(chat: Long, replyTo: Int): Unit =
		MornyCoeur.extra exec SendMessage(
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
