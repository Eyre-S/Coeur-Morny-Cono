package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText

class ClientPhotoMedia (
	
	override val mediaData: ClientMediaData,
	override val caption: Option[MessageText] = None,
	
) extends AbstractClientMedia {
	
	override def mediaType: String = "photo"
	
}
