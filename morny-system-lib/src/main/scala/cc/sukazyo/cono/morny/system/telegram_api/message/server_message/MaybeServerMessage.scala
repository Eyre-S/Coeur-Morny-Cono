package cc.sukazyo.cono.morny.system.telegram_api.message.server_message

import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat

trait MaybeServerMessage (
	
	val messageID: Long,
	val chat: Chat,
	
)
