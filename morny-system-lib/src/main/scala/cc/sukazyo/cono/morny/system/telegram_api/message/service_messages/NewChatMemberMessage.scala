package cc.sukazyo.cono.morny.system.telegram_api.message.service_messages

import cc.sukazyo.cono.morny.system.telegram_api.message.Message
import com.pengrad.telegrambot.model.User

trait NewChatMemberMessage (
	members: List[User]
) extends Message
