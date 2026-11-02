package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.Standardize.FileID

import java.io.File

/** A media file that can be sent via Telegram Bot API.
  *
  * Media file has no specific file type (we are talking is, 'photo', 'video', etc.), it is
  * just an abstraction of datas.
  *
  * > If we are talking about *file type* below, remember that means types like "photo",
  * > "video", "document", etc., not the implementation type like IDBased, FileBased, etc.
  * >
  * > If I want to say an IDBased file, for avoid confusion, I will say that it's
  * > *implementation* (or simplify, *impl*) is IDBased.
  *
  * Specific implementation of file data have different features:
  *
  * ### [[ClientMediaData$.IDBased]]
  *
  * This impl of media references to a file that already exists on Telegram servers, so that,
  * it can be sent without uploading the file again, and it has no limitation about file size.
  *
  * But it has its own limitations: Its filename cannot be changed; It cannot be reused with
  * another file type (like, a photo cannot be resent as a document); And the thumbnail cannot
  * be resent at all.
  * 
  * This impl of media requires [[FileID]], not [[cc.sukazyo.cono.morny.system.telegram_api.Standardize.FileUniqueID]].
  * For more details about the ID specification and limitations, see their docs.
  *
  * ### [[ClientMediaData$.FileBased]]:
  *
  * This type of media references to a file that exists on your local filesystem. It will be
  * uploaded to send.
  *
  * Using Java native [[File]] makes its filename can be fill automatically. If your filename
  * is sensitive, you may want to overwrite it.
  *
  * ### [[ClientMediaData$.ByteArrayBased]]:
  *
  * It directly receives the binary (bytes array) as the media file. Most flexible, and will
  * be uploaded to send.
  *
  * ## File uploading limit
  *
  * For any media file that is going to be uploaded, Telegram Bot API has a size limit.
  * Normally, under official endpoint, it is 50MB. For self-hosted Bot API, it is 2000MB. If
  * you are using TDLib/MTProto as backend, it may even larger.
  */
sealed trait ClientMediaData

object ClientMediaData {
	
	/** A media file that references to a file that already exists on Telegram servers.
	  *
	  * Media type on the Telegram server must be the same as the media type that is going to
	  * send. Specially, thumbnails can never use this type of file.
	  */
	case class IDBased (fileId: FileID) extends ClientMediaData
	def apply (fileId: String): IDBased = IDBased(fileId)
	
	/** A java native [[File]] as media file.
	  * 
	  * Will be uploaded so that it has size limits.
	  * 
	  * The filename will be automatically filled from the [[File]] object.
	  * 
	  * @see [[ClientMediaData]] about size limits.
	  * @param file Java [[File]] object, must be readable for this program.
	  */
	case class FileBased (file: File) extends ClientMediaData
	def apply (file: File): FileBased = FileBased(file)
	
	/** A media file's binary in a byte array.
	  *
	  * Will be uploaded so that it has size limits.
	  *
	  * @see [[ClientMediaData]] about size limits.
	  */
	case class ByteArrayBased (byteArray: Array[Byte]) extends ClientMediaData
	def apply (byteArray: Array[Byte]): ByteArrayBased = ByteArrayBased(byteArray)
	
}
