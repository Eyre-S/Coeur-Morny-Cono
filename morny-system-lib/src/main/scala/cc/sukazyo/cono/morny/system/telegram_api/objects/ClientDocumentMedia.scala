package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText
import com.pengrad.telegrambot.model.request.InputMediaDocument

class ClientDocumentMedia (
	
	// extended properties
	override val mediaData: ClientMediaData,
	override val caption: Option[MessageText] = None,
	
	// new owned properties
	val thumbnail: Option[ClientMediaData] = None
	
) extends AbstractClientMedia[InputMediaDocument] {
	
	override def mediaType: String = "document"
	
	override def toNative: InputMediaDocument = {
		val native = mediaData match {
			case ClientMediaData.IDBased(fileId) =>
				new InputMediaDocument(fileId)
			case ClientMediaData.FileBased(file) =>
				new InputMediaDocument(file)
			case ClientMediaData.ByteArrayBased(byteArray) =>
				new InputMediaDocument(byteArray)
		}
		this.decorateNative(native)
		native
	}
	
}
