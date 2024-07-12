package cc.sukazyo.cono.morny.system.telegram_api

object Standardize {
	
	type UserID = Long
	type ChatID = Long
	type MessageID = Int
	
	val CHANNEL_SPEAKER_MAGIC_ID = 136817688
	
	val MASK_BOTAPI_FORMATTED_ID: Long = -1000000000000L
	
}
