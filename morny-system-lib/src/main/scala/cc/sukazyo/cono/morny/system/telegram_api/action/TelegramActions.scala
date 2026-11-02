package cc.sukazyo.cono.morny.system.telegram_api.action

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSendRequest
import cc.sukazyo.cono.morny.system.telegram_api.account.AbstractBotAccount
import cc.sukazyo.cono.morny.system.telegram_api.message.{SendableMessage, UnsupportedForSendException}
import com.pengrad.telegrambot.request.BaseRequest
import com.pengrad.telegrambot.response.BaseResponse

trait TelegramActions {
	this: AbstractBotAccount =>
	
	@throws[ClientRequestException]
	@throws[UnsupportedForSendException]
	def sendMessage [Q <: NativeSendRequest[T, R], T <: BaseRequest[T, R], R <: BaseResponse]
	(message: SendableMessage[Q, T, R]): R = {
		this.exec(message.getSendRequest.request)
	}
	
}
