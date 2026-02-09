package cc.sukazyo.cono.morny.system.telegram_api.objects

import java.io.File

sealed trait ClientMedia {
	
	def enrich: ClientRichMedia = ClientRichMedia(this)
	
}

object ClientMedia {
	
	class IDBased (val fileId: String) extends ClientMedia
	def apply (fileId: String): IDBased = new IDBased(fileId)
	
	class FileBased (val file: File) extends ClientMedia
	def apply (file: File): FileBased = new FileBased(file)
	
	class ByteArrayBased (val byteArray: Array[Byte]) extends ClientMedia
	def apply (byteArray: Array[Byte]): ByteArrayBased = new ByteArrayBased(byteArray)
	
}
