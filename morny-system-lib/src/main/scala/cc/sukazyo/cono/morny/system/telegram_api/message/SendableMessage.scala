package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSendRequest
import cc.sukazyo.cono.morny.system.telegram_api.account.BotAccount
import cc.sukazyo.cono.morny.system.telegram_api.action.ClientRequestException
import com.pengrad.telegrambot.request.{AbstractSendRequest, BaseRequest}
import com.pengrad.telegrambot.response.BaseResponse

/** This message type is able to send via
  * [[cc.sukazyo.cono.morny.system.telegram_api.action.TelegramActions.sendMessage()]].
  *
  * @since 2.0.0-alpha22
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
	
	/** Get a [[NativeSendRequest]] that can send this message.
	  *
	  * ## Implementation note
	  *
	  * Do not override this method. Override [[generateBaseSendRequest]] and
	  * [[decorateSendRequest]] instead.
	  *
	  * @since 2.0.0-alpha22
	  *
	  * @return A fully decorated [[AbstractSendRequest]] that can be sent to Telegram API.
	  */
	def getSendRequest: T = {
		val req = this.generateBaseSendRequest
		this.decorateSendRequest(req)
		req
	}
	
	/** Get a [[NativeSendRequest]] that can send this message.
	  *
	  * It may lack some necessary information, which will be filled by [[decorateSendRequest]].
	  * 
	  * ## Implementation note
	  *
	  * Do not call [[decorateSendRequest]] in this method.
	  *
	  * @since 2.0.0-alpha22
	  *
	  * @param sendContext Information that helps to build an [[AbstractSendRequest]].
	  * @return Basic [[NativeSendRequest]]. Much information may be missing, requires a
	  *         [[decorateSendRequest decorate]] to complete.
	  */
	@throws[UnsupportedForSendException]
	def generateBaseSendRequest: T
	
	/** Send via a Telegram [[BotAccount]].
	  *
	  * On success, it will return the response from Telegram API. The specific type of the
	  * response is determined by the type of this message.
	  *
	  * @since 2.0.0-alpha22
	  *
	  * @return The response from Telegram API.
	  * @throws ClientRequestException Request is sent but failed. Maybe a rejected by API, or
	  *                                a network issue occurred.
	  * @throws UnsupportedForSendException If the message cannot be sent. Maybe some parameters
	  *                                     conflicts, or some necessary information is missing,
	  *                                     or some parameters is not supported to send yet.
	  */
	@throws[ClientRequestException]
	@throws[UnsupportedForSendException]
	def send (using account: BotAccount): Resp =
		account.sendMessage(this)
	
}
