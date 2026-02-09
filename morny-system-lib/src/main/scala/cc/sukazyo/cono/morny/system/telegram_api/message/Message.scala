package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.{Chat, ThreadableChat}
import com.pengrad.telegrambot.model.request.ReplyParameters
import com.pengrad.telegrambot.request.AbstractSendRequest

trait Message extends MaybeSendableMessage {
	
	def chat: Chat
	
	def replyParameters: Option[ReplyParameters]
	
	//	def replyMarkup: Null
	//	def inlineKeyboard: this.replyMarkup.type = replyMarkup
	
	override def decorateSendRequest[D_REQ <: AbstractSendRequest[D_REQ]] (request: AbstractSendRequest[D_REQ], sendContext: SendMessageContext): Unit = {
		
		this.chat match
			case chatThread: ThreadableChat =>
				request.messageThreadId(chatThread.threadId)
			case _ =>
		
		this.replyParameters.map(request.replyParameters)
		
	}
	
}
