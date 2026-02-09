package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText

class ClientRichMedia (_media: ClientMedia, _caption: Option[MessageText]) {
	
	def media: ClientMedia = _media
	def caption: Option[MessageText] = _caption
//	def showCaptionAboveMedia: Boolean
//	def spoiler: Boolean
	
	def unwrap: ClientMedia = media
	
}

object ClientRichMedia {
	
	def apply (clientMedia: ClientMedia) =
		new ClientRichMedia(clientMedia, _caption = None)
	
	def apply (clientMedia: ClientMedia, caption: MessageText) =
		new ClientRichMedia(clientMedia, _caption = Some(caption))
	
}
