package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSendRequest
import cc.sukazyo.cono.morny.system.telegram_api.account.BotAccount
import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import com.pengrad.telegrambot.request.{AbstractSendRequest, BaseRequest}
import com.pengrad.telegrambot.response.BaseResponse

/** This message type is able to send via
  * [[cc.sukazyo.cono.morny.system.telegram_api.action.TelegramActions.sendMessage()]].
  *
  * @todo I need to explicitly declare all type parameters in the implementations, like:
  *
  *       ```scala
  *       class TextMessage extends SendableMessage[NativeSimpleSendRequest[SendMessage], SendMessage, SendResponse]
  *       ```
  *
  *       Works very well but too complex for implementation declarations.
  *
  *       But the simple way like
  *       `class TextMessage extends SendableMessage[NativeSimpleSendRequest[SendMessage]]`
  *       should works too, since the `NativeSimpleSendRequest[SendMessage]` already has type
  *       `SendMessage` and `SendResponse` implicitly.
  *
  *       This should be able to implemented by using type parameters rather than generics. But
  *       I cannot make it works...
  */
trait SendableMessage [T <: NativeSendRequest[Req, Resp], Req <: BaseRequest[Req, Resp], Resp <: BaseResponse]
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
	def getSendRequest (sendContext: SendMessageContext): T
	
	def send (account: BotAccount): Resp =
		account.sendMessage(this)
	
}
