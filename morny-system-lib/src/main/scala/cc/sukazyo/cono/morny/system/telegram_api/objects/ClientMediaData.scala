package cc.sukazyo.cono.morny.system.telegram_api.objects

import java.io.File

sealed trait ClientMediaData

object ClientMediaData {
	
	case class IDBased (fileId: String) extends ClientMediaData
	def apply (fileId: String): IDBased = IDBased(fileId)
	
	case class FileBased (file: File) extends ClientMediaData
	def apply (file: File): FileBased = FileBased(file)
	
	case class ByteArrayBased (byteArray: Array[Byte]) extends ClientMediaData
	def apply (byteArray: Array[Byte]): ByteArrayBased = ByteArrayBased(byteArray)
	
}
