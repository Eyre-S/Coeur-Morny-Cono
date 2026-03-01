package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText
import com.pengrad.telegrambot.model.request.InputMediaPhoto

class ClientPhotoMedia (
	
	override val mediaData: ClientMediaData,
	override val caption: Option[MessageText] = None,
	
) extends AbstractClientMedia[InputMediaPhoto] {
	
	override def mediaType: String = "photo"
	
	override def toNative: InputMediaPhoto = {
		val native = mediaData match {
			case ClientMediaData.IDBased(fileId) =>
				new InputMediaPhoto(fileId)
			case ClientMediaData.FileBased(file) =>
				new InputMediaPhoto(file)
			case ClientMediaData.ByteArrayBased(byteArray) =>
				new InputMediaPhoto(byteArray)
		}
		this.decorateNative(native)
		native
	}
	
}
