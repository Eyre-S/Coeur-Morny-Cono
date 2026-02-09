package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeMessage
import cc.sukazyo.cono.morny.system.telegram_api.Standardize.{ChatID, MessageThreadID}
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import com.pengrad.telegrambot.model.request.ReplyParameters

object Messages {
	
	def create (chat: Chat, replyParameters: ReplyParameters): BaseCreatingMessage =
		BaseCreatingMessage(chat, Some(replyParameters))
	
	def create (chat: Chat): BaseCreatingMessage =
		BaseCreatingMessage(chat, None)
	
	def create (chatId: ChatID, threadId: MessageThreadID, replyParameters: ReplyParameters): BaseCreatingMessage =
		BaseCreatingMessage(Chat(chatId, threadId), Some(replyParameters))
	
	def create (chatId: ChatID, threadId: MessageThreadID): BaseCreatingMessage =
		create(Chat(chatId, threadId))
	
	def create (chatId: ChatID, replyParameters: ReplyParameters): BaseCreatingMessage =
		create(Chat(chatId), replyParameters)
	
	def create (chatId: ChatID): BaseCreatingMessage =
		create(Chat(chatId))
	
	def derive (baseMessage: Message): BaseCreatingMessage =
		BaseCreatingMessage(baseMessage.chat, baseMessage.replyParameters)
	
	def derive (baseMessage: NativeMessage): BaseCreatingMessage =
		BaseCreatingMessage(
			Chat.from(baseMessage),
			Some(ReplyParameters(baseMessage.messageId))
		)
	
	def deriveNoReply (baseMessage: NativeMessage): BaseCreatingMessage =
		BaseCreatingMessage(Chat.from(baseMessage), None)
	
}
