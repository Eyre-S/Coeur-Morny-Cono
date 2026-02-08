package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import com.pengrad.telegrambot.model.request.ReplyParameters

class BaseCreatingMessage (
	override val chat: Chat,
	override val replyParameters: Option[ReplyParameters]
) extends Message
