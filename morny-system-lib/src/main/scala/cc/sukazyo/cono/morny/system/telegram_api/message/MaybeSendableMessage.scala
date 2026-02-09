package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import com.pengrad.telegrambot.request.AbstractSendRequest

trait MaybeSendableMessage {
	
	def decorateSendRequest
	[D_REQ <: AbstractSendRequest[D_REQ]]
	(request: AbstractSendRequest[D_REQ], sendContext: SendMessageContext)
	: Unit
	
}
