package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.objects.ClientMediaData.{ByteArrayBased, FileBased, IDBased}

import java.io.File

object Medias {
	
	def of (fileId: String): BaseCreatingMedia =
		new BaseCreatingMedia(ClientMediaData.IDBased(fileId), None)
	
	def of (file: File): BaseCreatingMedia =
		new BaseCreatingMedia(ClientMediaData.FileBased(file), None)
	
	def of (bytes: Array[Byte]): BaseCreatingMedia =
		new BaseCreatingMedia(ClientMediaData.ByteArrayBased(bytes), None)
	
	def of (data: ClientMediaData): BaseCreatingMedia =
		data match {
			case idBased: IDBased => this.of(idBased.fileId)
			case fileBased: FileBased => this.of(fileBased.file)
			case byteArrayBased: ByteArrayBased => this.of(byteArrayBased.byteArray)
		}
	
}
