package cc.sukazyo.cono.morny.system.telegram_api.objects

import java.io.File

sealed trait ClientMediaData

object ClientMediaData {
	
	class IDBased (val fileId: String) extends ClientMediaData
	def apply (fileId: String): IDBased = new IDBased(fileId)
	
	class FileBased (val file: File) extends ClientMediaData
	def apply (file: File): FileBased = new FileBased(file)
	
	class ByteArrayBased (val byteArray: Array[Byte]) extends ClientMediaData
	def apply (byteArray: Array[Byte]): ByteArrayBased = new ByteArrayBased(byteArray)
	
}
