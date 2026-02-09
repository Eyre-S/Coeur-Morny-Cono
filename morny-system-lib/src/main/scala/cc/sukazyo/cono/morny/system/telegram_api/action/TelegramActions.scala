package cc.sukazyo.cono.morny.system.telegram_api.action

import cc.sukazyo.cono.morny.system.telegram_api.account.AbstractBotAccount
import cc.sukazyo.cono.morny.system.telegram_api.message.{SendableMessage, UnsupportedForSendException}
import com.pengrad.telegrambot.request.AbstractSendRequest
import com.pengrad.telegrambot.response.SendResponse

trait TelegramActions {
	this: AbstractBotAccount =>
	
	@throws[ClientRequestException]
	@throws[UnsupportedForSendException]
	def sendMessage [REQ <: AbstractSendRequest[REQ]] (message: SendableMessage[REQ]): SendResponse =  {
		val sendContext = SendMessageContext(this)
		val sendRequest: AbstractSendRequest[REQ] = message.getSendRequest(sendContext)
		message.decorateSendRequest(sendRequest, sendContext)
		this.exec(sendRequest)
	}
	
}
