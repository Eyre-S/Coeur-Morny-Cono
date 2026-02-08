package cc.sukazyo.cono.morny.system.telegram_api.message

import com.pengrad.telegrambot.model.request.ReplyParameters

trait Message (
	
	val replyParameters: ReplyParameters,
	
	val replyMarkup: Null
	
) {
	
	def inlineKeyboard: this.replyMarkup.type = replyMarkup
	
}
