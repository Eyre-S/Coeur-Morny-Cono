package cc.sukazyo.cono.morny.util.tgapi

import com.pengrad.telegrambot.model.{Chat, Message, User}

object Standardize {
	
	type UserID = Long
	type ChatID = Long
	type MessageID = Int
	
	val CHANNEL_SPEAKER_MAGIC_ID = 136817688
	
	val MASK_BOTAPI_ID: Long = -1000000000000
	
}
