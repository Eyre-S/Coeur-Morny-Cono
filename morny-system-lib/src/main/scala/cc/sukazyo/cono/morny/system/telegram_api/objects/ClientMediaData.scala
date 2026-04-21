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
  * ### [[ClientMediaData$.FileBased]]: This type of media references to a file that exists on
  *   your local filesystem. It requires an uploading thus it is slower that
  */
sealed trait ClientMediaData

object ClientMediaData {
	
	case class IDBased (fileId: FileID) extends ClientMediaData
	def apply (fileId: String): IDBased = IDBased(fileId)
	
	case class FileBased (file: File) extends ClientMediaData
	def apply (file: File): FileBased = FileBased(file)
	
	case class ByteArrayBased (byteArray: Array[Byte]) extends ClientMediaData
	def apply (byteArray: Array[Byte]): ByteArrayBased = ByteArrayBased(byteArray)
	
}
