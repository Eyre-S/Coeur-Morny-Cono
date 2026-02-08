package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText

trait MediaMessage (
	
	val captionText: MessageText,
	val isCaptionAboveMedia: Boolean,
	val isMediaSpoiler: Boolean,
	
) extends Message
