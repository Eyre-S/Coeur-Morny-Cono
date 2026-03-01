package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText

class BaseCreatingMedia (
	override val mediaData: ClientMediaData,
	override val caption: Option[MessageText],
) extends AbstractCreatingMedia {
	
	def caption (text: MessageText): BaseCreatingMedia =
		new BaseCreatingMedia(
			mediaData = this.mediaData,
			caption = Some(text),
		)
	
}
