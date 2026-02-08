package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import com.pengrad.telegrambot.model.request.ReplyParameters

trait Message (
	
	val chat: Chat,
	val replyParameters: ReplyParameters,
	
	val replyMarkup: Null
	
) {
	
	def inlineKeyboard: this.replyMarkup.type = replyMarkup
	
}
