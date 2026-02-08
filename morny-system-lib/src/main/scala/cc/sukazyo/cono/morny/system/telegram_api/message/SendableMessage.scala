package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.ThreadableChat
import com.pengrad.telegrambot.request.AbstractSendRequest

/** This message type is able to send via
  * [[cc.sukazyo.cono.morny.system.telegram_api.action.TelegramActions.sendMessage()]].
  */
trait SendableMessage [REQ <: AbstractSendRequest[REQ]] extends Message {
	
	/** Get an [[AbstractSendRequest]] that can send this message.
	  * 
	  * ## Implementation note
	  * 
	  * When implementing this method, it does not need (and not recommended) to call
	  * [[decorateSendRequest]] this method. The
	  * [[cc.sukazyo.cono.morny.system.telegram_api.action.TelegramActions.sendMessage]] will
	  * call it.
	  * 
	  * @param sendContext Information that helps to build an [[AbstractSendRequest]].
	  * @return
	  */
	def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[REQ]
	
	def decorateSendRequest [D_REQ <: AbstractSendRequest[D_REQ]] (request: AbstractSendRequest[D_REQ], sendContext: SendMessageContext): Unit = {
		
		this.chat match
			case chatThread: ThreadableChat =>
				request.messageThreadId(chatThread.threadId)
			case _ =>
		
		this.replyParameters.map(request.replyParameters)
		
	}
	
}
