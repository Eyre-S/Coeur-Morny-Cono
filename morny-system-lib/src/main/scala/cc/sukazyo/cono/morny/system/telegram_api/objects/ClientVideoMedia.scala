package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText

class ClientVideoMedia (
	
	override val mediaData: ClientMediaData,
	override val caption: Option[MessageText] = None,
	
	val thumbnail: Option[ClientMediaData],
	val cover: Option[ClientMediaData],
	
	val startTimestamp: Option[Int],
	val width: Option[Int],
	val height: Option[Int],
	val duration: Option[Int],
	val supportsStreaming: Option[Boolean],
	
) extends AbstractClientMedia {
	
	override def mediaType: String = "video"
	
}
