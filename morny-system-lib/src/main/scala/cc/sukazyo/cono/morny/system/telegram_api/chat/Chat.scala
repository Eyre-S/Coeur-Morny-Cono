package cc.sukazyo.cono.morny.system.telegram_api.chat

import cc.sukazyo.cono.morny.system.telegram_api.Standardize.{ChatID, MessageThreadID}

trait Chat {
	def id: ChatID
}

object Chat {
	
	class DummyClientChat (
		override val id: ChatID,
	) extends Chat
	
	class DummyClientThreadableChat (
		override val id: ChatID,
		override val threadId: MessageThreadID
	) extends ThreadableChat
	
	def apply (chatID: ChatID): DummyClientChat =
		DummyClientChat(chatID)
	
	def apply (chatId: ChatID, chatThreadId: MessageThreadID): DummyClientThreadableChat =
		DummyClientThreadableChat(chatId, chatThreadId)
	
}
