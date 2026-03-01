package cc.sukazyo.cono.morny.system.telegram_api.objects

import java.io.File

object Medias {
	
	def of (fileId: String): BaseCreatingMedia =
		new BaseCreatingMedia(ClientMediaData.IDBased(fileId), None)
	
	def of (file: File): BaseCreatingMedia =
		new BaseCreatingMedia(ClientMediaData.FileBased(file), None)
	
	def of (bytes: Array[Byte]): BaseCreatingMedia =
		new BaseCreatingMedia(ClientMediaData.ByteArrayBased(bytes), None)
	
}
