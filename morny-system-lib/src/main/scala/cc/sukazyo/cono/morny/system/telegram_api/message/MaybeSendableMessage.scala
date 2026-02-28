package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSendRequest
import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext

trait MaybeSendableMessage {
	
	def decorateSendRequest (request: NativeSendRequest[?, ?], sendContext: SendMessageContext): Unit
	
}
