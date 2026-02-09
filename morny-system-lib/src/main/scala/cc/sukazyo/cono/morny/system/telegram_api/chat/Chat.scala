package cc.sukazyo.cono.morny.system.telegram_api.chat

import cc.sukazyo.cono.morny.system.telegram_api.Natives.{NativeChat, NativeMessage}
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
	
	/** Translate a Telegram API's [[NativeChat]] to this API's [[Chat]] object.
	  * 
	  * ## Warning
	  * 
	  * Using this translate method may lose the information about [[MessageThreadID]]. Due to
	  * the [[NativeChat]] does not contain [[MessageThreadID]] related information (it is
	  * stored in [[NativeMessage]] in official API interface). For any cases possible, use
	  * [[from(NativeMessage)]] is better.
	  */
	def from (baseChat: NativeChat): Chat =
		DummyClientChat(baseChat.id)
	
	def from (baseMessage: NativeMessage): Chat =
		if (baseMessage.messageThreadId == null)
			DummyClientChat(baseMessage.chat.id)
		else
			DummyClientThreadableChat(baseMessage.chat.id, baseMessage.messageThreadId)
	
}
