package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.account.BotAccount
import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import com.pengrad.telegrambot.request.AbstractSendRequest
import com.pengrad.telegrambot.response.SendResponse

/** This message type is able to send via
  * [[cc.sukazyo.cono.morny.system.telegram_api.action.TelegramActions.sendMessage()]].
  */
trait SendableMessage [REQ <: AbstractSendRequest[REQ]]
	extends Message {
	
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
	@throws[UnsupportedForSendException]
	def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[REQ]
	
	def send (account: BotAccount): SendResponse =
		account.sendMessage(this)
	
}
