package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import com.pengrad.telegrambot.model.request.ReplyParameters

trait Message {
	
	def chat: Chat
	def replyParameters: Option[ReplyParameters]
	
//	def replyMarkup: Null
//	def inlineKeyboard: this.replyMarkup.type = replyMarkup
	
}

object Message {
	
	def create (chat: Chat, replyParameters: ReplyParameters): BaseCreatingMessage =
		BaseCreatingMessage(chat, Some(replyParameters))
	
//	def create (chat)
//	def create (chatId, threadId, replyParameters)
//	def create (chatId, threadId)
//	def create (chatId, replyParameters)
//	def create (chatId)

}
