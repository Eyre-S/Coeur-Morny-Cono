package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText
import com.pengrad.telegrambot.model.request.InputMediaVideo

class ClientVideoMedia (
	
	// common properties
	override val mediaData: ClientMediaData,
	override val caption: Option[MessageText] = None,
	
	// owned properties
	val thumbnail: Option[ClientMediaData],
	val cover: Option[ClientMediaData],
	
	val startTimestamp: Option[Int],
	val width: Option[Int],
	val height: Option[Int],
	val duration: Option[Int],
	val supportsStreaming: Option[Boolean],
	
) extends AbstractClientMedia[InputMediaVideo] {
	
	override def mediaType: String = "video"
	
	override def toNative: InputMediaVideo = {
		val native = mediaData match {
			case ClientMediaData.IDBased(fileId) =>
				new InputMediaVideo(fileId)
			case ClientMediaData.FileBased(file) =>
				new InputMediaVideo(file)
			case ClientMediaData.ByteArrayBased(byteArray) =>
				new InputMediaVideo(byteArray)
		}
		this.decorateNative(native)
		native
	}
	
}

object ClientVideoMedia {
	
	trait CreateOps {
		this: AbstractCreatingMedia =>
		
		def video: ClientVideoMedia =
			new ClientVideoMedia(this.mediaData, this.caption, None, None, None, None, None, None, None)
		
	}
	
}
