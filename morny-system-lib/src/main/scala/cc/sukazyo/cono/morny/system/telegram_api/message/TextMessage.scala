package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.text.Text

trait TextMessage (
	
	val text: Text,
	val linkPreviewOption: Option[?],
	
) extends Message
