package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.command.Encryptor.*
import cc.sukazyo.cono.morny.bot.command.ICommandAlias.ListedAlias
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.CommonEncrypt
import cc.sukazyo.cono.morny.util.CommonEncrypt.*
import cc.sukazyo.cono.morny.util.ConvertByteHex.toHex
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
import cc.sukazyo.messiva.logger.Logger
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.model.{Message, PhotoSize, Update}
import com.pengrad.telegrambot.request.{GetFile, SendDocument, SendMessage, SendSticker}

import java.io.IOException
import java.net.{URLDecoder, URLEncoder}
import java.nio.charset.{Charset, IllegalCharsetNameException, UnsupportedCharsetException}
import java.util.Base64
import scala.language.postfixOps
import scala.util.boundary
import scala.util.boundary.break

object Encryptor {
	
	sealed trait Encryptable {
		@throws[IOException]
		def binary: Bin
	}
	
	object Encryptable {
		
		def fromPhotos (photos: Array[PhotoSize])(using MornyCoeur): Either[PhotoSizeException, InputTFile] = {
			var _photo_origin: PhotoSize = null
			var _photo_size: Long = 0
			for (size <- photos)
				val _size = (size.width longValue) * size.height
				if (_photo_size < _size)
					_photo_origin = size
					_photo_size = _size
			if (_photo_origin eq null) return Left(PhotoSizeException("no photo from api."))
			import cc.sukazyo.cono.morny.util.UseRandom.rand_id
			Right(InputTFile(_photo_origin.fileId, s"photo$rand_id.png"))
		}
		
		def fromAttachments
		(onPhotoSizeException: PhotoSizeException => Unit)
		(message: Message, withTextBody: Boolean = false)
		(using Charset, Logger, MornyCoeur): Option[Encryptable] = {
			if message eq null then return None
			if (message.document ne null) {
				return Some(InputTFile(message.document.fileId, message.document.fileName))
			} else if (message.photo ne null) {
				Encryptable.fromPhotos(message.photo) match {
					case Right(right) => return Some(right)
					case Left(err) => onPhotoSizeException(err)
				}
			} else if (withTextBody && message.text != null) {
				return Some(InputText(message.text))
			}
			None
		}
		
	}
	
	case class InputText (str: String)(using charset: Charset) extends Encryptable {
		override def binary: Bin = str.getBytes(charset)
	}
	
	sealed trait EncryptableFile extends Encryptable {
		def filename: String
	}
	
	case class InputFile (override val binary: Bin, override val filename: String) extends EncryptableFile
	
	case class InputTFile (fileId: String, override val filename: String)(using coeur: MornyCoeur)
	extends EncryptableFile {
		@throws[IOException]
		override def binary: Bin = coeur.account.getFileContent(
			(coeur.account `exec` GetFile(fileId)).file
		)
	}
	
	class PhotoSizeException (message: String) extends Exception(s"FileProcess error: PhotoSize:\n\t$message") {
		def log ()(using logger: Logger, coeur: MornyCoeur): Unit = {
			logger.warn(this.getMessage)
			coeur.daemons.reporter.exception(this, "FileProcess error: PhotoSize")
		}
	}
	
	case class OutputFile (file: Bin, filename: String)
	
	trait OutputTextLike {
		def text: String
	}
	
	case class OutputText (text: String) extends OutputTextLike
	
	case class OutputHash (text: String) extends OutputTextLike
	
	@throws[IOException]
	def doEncrypt (
		source: Encryptable,
		processor: Bin => Bin,
		filenameProcessor: String => String
	)(using charset: Charset): OutputTextLike | OutputFile = {
		source match {
			case inputFile: EncryptableFile =>
				OutputFile(processor(inputFile.binary), filenameProcessor(inputFile.filename))
			case inputText: InputText =>
				OutputText(String(processor(inputText.binary), charset))
		}
	}
	
	@throws[IOException]
	def doHash (uppercase: Boolean)(source: Encryptable, processor: Bin => Bin): OutputHash = {
		val hashed = processor(source.binary).toHex
		OutputHash(if uppercase then hashed.toUpperCase else hashed)
	}
	
	class NoParamValueException (key: String)
		extends Exception (s"No value defined for param key $key")
	
}

/** Provides Telegram Command __`/encrypt`__. */
class Encryptor (using coeur: MornyCoeur) extends ITelegramCommand {
	
	override val name: String = "encrypt"
	override val aliases: Array[ICommandAlias] | Null = Array(ListedAlias("enc"))
	override val paramRule: String = "[algorithm|(l)] [(uppercase)]"
	override val description: String = "通过指定加密/解密/哈希算法处理信息"
	
	private def echoNoContent ()(using message: Message): Unit = {
		coeur.account `exec` SendMessage(
			message.chat.id,
			"<i><u>null</u></i>"
		).parseMode(ParseMode HTML).replyToMessageId(message.messageId)
	}
	
	private def echoConflict ()(using message: Message): Unit = {
		coeur.account `exec` SendSticker(
			message.chat.id,
			TelegramStickers.ID_CONFLICT
		).replyToMessageId(message.messageId)
	}
	
	private def echoUnsupported ()(using message: Message): Unit = {
		coeur.account `exec` SendSticker(
			message.chat.id,
			TelegramStickers.ID_404
		).replyToMessageId(message.messageId)
	}
	
	private def echoInternalError ()(using message: Message): Unit = {
		coeur.account `exec` SendSticker(
			message.chat.id,
			TelegramStickers.ID_OVERHEAT
		).replyToMessageId(message.messageId)
	}
	
	override def execute (using command: InputCommand, event: Update): Unit = boundary {
		given Logger = logger
		given Message = event.message
		
		val args = command.args
		
		// show a simple help page
		if ((args isEmpty) || ((args(0) `equals` "l") && (args.length == 1)))
			echoHelp(event.message.chat.id, event.message.messageId)
			return
		
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
		
		def paramTagExists (tags: List[String]): Boolean = {
			params.exists { param =>
				tags.exists { tag =>
					if param.text `equalsIgnoreCase` tag then {
						param.used()
						true
					} else false
				}
			}
		}
		
		def paramGetValue (tags: List[String]): Option[String] = boundary {
			val iterator = params.iterator
			while (iterator.hasNext) {
				val it = iterator.next()
				if (tags.exists(_.equalsIgnoreCase(it.text))) {
					it.used()
					iterator.nextOption() match {
						case Some(next) =>
							next.used()
							break(Some(next.text))
						case None => throw new NoParamValueException(it.text)
					}
				}
			}
			None
		}
		
		/** mod arg: If exists, encrypted results that ignore-cases (like hashes) will force use UPPERCASE. */
		lazy val mod_uppercase = paramTagExists("uppercase" :: "upper" :: "u" :: Nil)
		
		/** mod arg: If exists, encrypted results will always output as attached file. */
		lazy val mod_file: Boolean = paramTagExists("file" :: "f" :: Nil)
		
		/** value arg: override file name when output as a file. */
		@throws[IllegalArgumentException]
		lazy val param_filename: Option[String] = paramGetValue("name" :: Nil)
		
		/** env arg: force different charset to use. */
		given mod_charset: Charset =
			params.find(_.text.startsWith("-e")) match
				case Some(param) =>
					param.used()
					val charsetName = param.text.drop("-e".length)
					try Charset.forName(charsetName)
					catch case _: (IllegalCharsetNameException | UnsupportedCharsetException) =>
						coeur.account `exec` SendMessage(
							event.message.chat.id,
							// language=html
							s"<b>Unsupported Charset:</b> <code>${h(charsetName)}</code>".stripMargin
						).replyToMessageId(event.message.messageId)
						break()
				case None => CommonEncrypt.ENCRYPT_STANDARD_CHARSET
//		lazy val mod_enc_charset = modCharsetFind("-ee")
//		lazy val mod_dec_charset = modCharsetFind("-ed")
		
		// BLOCK: get input
		/** User input data that will be encrypted.
		  *
		  * Will be loaded from (sorted by priority):
		  *  1. texts that following the command. (the second & later lines of command message)
		  *  2. attachments of command message.
		  *  3. attachments of replied message.
		  *  4. message full texts of replied message. (rich styles will not be formatted)
		  *
		  * Note that *source 1* and *source 2* is conflict, if both exists, an error will be occurred.
		  */
		val input: Encryptable = {
			val myFromAttachments = Encryptable.fromAttachments(onPhotoSizeException = { err =>
				err.log()
			})
			// message body data
			val dataFromParam =
				val data = command.data
				if (!data.isBlank) Some(InputText(data))
				else None
			// message attachments
			val dataFromAttachments = myFromAttachments(event.message, false)
			// replied message attachments || text body
			lazy val dataFromReplies = myFromAttachments(event.message.replyToMessage, true)
			
			if (dataFromParam.isDefined && dataFromAttachments.isDefined)
				echoConflict(); return
			dataFromParam
			.orElse(dataFromAttachments)
			.orElse(dataFromReplies) match {
				case Some(value) => value
				case None =>
					echoNoContent()
					return
			}
		}
		// END BLOCK: get input
		
		// BLOCK: encrypt
		//noinspection UnitMethodIsParameterless
		def myDoHash = doHash(mod_uppercase)
		val result: OutputTextLike|OutputFile = try args(0) match
			case "base64" | "base" | "baseu" | "b64" | "base64url" | "base64u" | "b64u" =>
				val _tool_b64 =
					if args(0) `contains` "u" then Base64.getUrlEncoder
					else Base64.getEncoder
				doEncrypt(
					input,
					_tool_b64.encode,
					n => n+".b64.txt"
				)
			case "base64decode"  | "based" | "baseud" | "base64d" | "b64d" | "base64url-decode" | "base64ud" | "b64ud" =>
				val _tool_b64d =
					if args(0) `contains` "u" then Base64.getUrlDecoder
					else Base64.getDecoder
				val mappedInput:Encryptable =
					if mod_file then
						input match {
							case textInput: InputText =>
								InputFile(textInput.binary, "data.base64")
							case _ => input
						}
					else input
				try { doEncrypt(
					mappedInput,
					_tool_b64d.decode,
					CommonEncrypt.lint_base64FileName
				) } catch case _: IllegalArgumentException =>
					echoUnsupported(); return
			case "urlencoder" | "urlencode" | "urlenc" | "url" =>
				input match
					case x: InputText =>
						OutputText(URLEncoder.encode(x.str, mod_charset))
					case _: EncryptableFile =>
						echoUnsupported(); return;
			case "urldecoder" | "urldecode" | "urldec" | "urld" =>
				input match
					case _: EncryptableFile =>
						echoUnsupported(); return;
					case x: InputText =>
						try { OutputText(URLDecoder.decode(x.str, mod_charset)) }
						catch case _: IllegalArgumentException =>
							echoUnsupported(); return
			case "md5" => myDoHash(input, MD5)
			case "sha1" => myDoHash(input, SHA1)
			case "sha256" => myDoHash(input, SHA256)
			case "sha512" => myDoHash(input, SHA512)
			case _ =>
				echoUnsupported(); return;
		catch
			case e: IOException =>
				this.echoInternalError()
				logger `warn` s"NetworkRequest error: TelegramFileAPI:\n\t${e.getMessage}"
				coeur.daemons.reporter.exception(e, "NetworkRequest error: TelegramFileAPI")
				return
		// END BLOCK: encrypt
		
		// warning if params not used:
		def lastCheckUnusedParams (): Boolean = {
			if params.exists(_.isUsed == false) then {
				coeur.account `exec` SendMessage(
					event.message.chat.id,
					// language=html
					s"""<b>Unknown Params:</b>
					   |${params.filterNot(_.isUsed).map(x => s" - <code>${h(x.text)}</code>").mkString("\n")}""".stripMargin
				).replyToMessageId(event.message.messageId).parseMode(ParseMode.HTML)
				true
			} else false
		}
		
		// output
		result match
			case _file: OutputFile =>
				try
					val filename = param_filename.getOrElse(_file.filename)
					if lastCheckUnusedParams() then return
					coeur.account `exec` SendDocument(
						event.message.chat.id,
						_file.file
					).fileName(
						filename
					).replyToMessageId(event.message.messageId)
				catch
					case e: NoParamValueException =>
						echoUnsupported()
			case _text: OutputTextLike =>
				if lastCheckUnusedParams() then return
				import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
				coeur.account `exec` SendMessage(
					event.message.chat.id,
					// language=html
					s"<pre><code>${h(_text.text)}</code></pre>"
				).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
		
	}
	
	/** echo help to a specific message in a specific chat.
	  *
	  * ## the help message
	  * The first paragraph lists available encrypt algorithms and its alias,
	  * each line have one algorithm where the first name highlighted is the
	  * main name and following is aliases separated with `,`.
	  * with the separator `---`, the second paragraph lists available mods
	  * for algorithms, displays with the same rule of algorithms, with an extra
	  * italic text following describes its usage environment.
	  *
	  * when output to telegram just like:
	  *
	  * > > <b>/enc &lt;algorithm&gt;</b> *<u>[arguments...]</u>*<br>
	  * > > **<u>&lt;data&gt;</u>**
	  * >
	  * > > 📎 <b>/enc &lt;algorithm&gt;</b> *<u>[arguments...]</u>*
	  * >
	  * > > ↪️ <b>/enc &lt;algorithm&gt;</b> *<u>[arguments...]</u>*
	  * >
	  * > --- Algorithms ---<br>
	  * > **base64**, base, b64<br>
	  * > **base64u**, baseu, base64u, b64u<br>
	  * > **base64decode**, based, base64d, b64d<br>
	  * > **base64url-decode**, baseud, base64ud, b64ud<br>
	  * > **urlencoder**, urlencode, urlenc, url<br>
	  * > **urldecoder**, urldecode, urldec, urld<br>
	  * > **sha1**<br>
	  * > **sha256**<br>
	  * > **sha512**<br>
	  * > **md5**<br>
	  * > --- Arguments ---<br>
	  * > ***uppercase***, upper, u *(sha1/sha256/sha512/md5 only)*<br>
	  * > ***-e<u>&lt;charset_name&gt;</u>*** *(base64/url encode/decode only)*<br>
	  * > ***file***, f *(base64 decode only)*<br>
	  * > ***name <u>&lt;filename&gt;</u>*** *(only when out a file)*<br>
	  */
	private def echoHelp(chat: Long, replyTo: Int): Unit =
		coeur.account `exec` SendMessage(
			chat,
			// language=html
			s"""<blockquote><b>/enc <u>&lt;algorithm&gt;</u></b> <u><i>[arguments...]</i></u>
			   |<b><u>&lt;data&gt;</u></b></blockquote>
			   |<blockquote>📎 <b>/enc <u>&lt;algorithm&gt;</u></b> <u><i>[arguments...]</i></u></blockquote>
			   |<blockquote>↪️ <b>/enc <u>&lt;algorithm&gt;</u></b> <u><i>[arguments...]</i></u></blockquote>
			   |--- Algorithms ---
			   |<b>base64</b>, base, b64
			   |<b>base64url</b>, baseu, base64u, b64u
			   |<b>base64decode</b>, based, base64d, b64d
			   |<b>base64url-decode</b>, baseud, base64ud, b64ud
			   |<b>urlencoder</b>, urlencode, urlenc, url
			   |<b>urldecoder</b>, urldecode, urldec, urld
			   |<b>sha1</b>
			   |<b>sha256</b>
			   |<b>sha512</b>
			   |<b>md5</b>
			   |--- Arguments ---
			   |<b><i>uppercase</i></b>, upper, u <i>(sha1/sha256/sha512/md5 only)</i>
			   |<b><i>-e<u>&lt;charset_name&gt;</u></i></b> <i>(base64/url encode/decode only)</i>
			   |<b><i>file</i></b>, f <i>(base64 decode only)</i>
			   |<b><i>name <u>&lt;filename&gt;</u></i></b> <i>(only when out a file)</i>"""
			.stripMargin
		).replyToMessageId(replyTo).parseMode(ParseMode HTML)
	
}
