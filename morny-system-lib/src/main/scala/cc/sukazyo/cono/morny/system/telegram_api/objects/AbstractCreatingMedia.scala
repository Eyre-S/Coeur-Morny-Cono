package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText

trait AbstractCreatingMedia {
	
	def mediaData: ClientMediaData
	
	def caption: Option[MessageText]
	
}
