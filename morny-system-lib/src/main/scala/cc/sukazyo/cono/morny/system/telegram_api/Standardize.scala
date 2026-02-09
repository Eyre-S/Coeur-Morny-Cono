package cc.sukazyo.cono.morny.system.telegram_api

object Standardize {
	
	type UserID = Long
	type ChatID = Long
	type MessageThreadID = Long
	type MessageID = Int
	type MessageGroupID = String
	
	type ServerTime = Int
	
	val CHANNEL_SPEAKER_MAGIC_ID: UserID = 136817688
	
	val MASK_BOTAPI_FORMATTED_ID: ChatID = -1000000000000L
	
	val NON_COMMITED_MESSAGE_ID: MessageID = 0
	
}
