package cc.sukazyo.cono.morny.system.telegram_api

import com.pengrad.telegrambot.model.{Chat, Message, User}
import com.pengrad.telegrambot.request.{AbstractSendRequest, BaseRequest, SendMediaGroup}
import com.pengrad.telegrambot.response.{BaseResponse, MessagesResponse, SendResponse}

object Natives {
	
	type NativeChat = Chat
	type NativeUser = User
	type NativeMessage = Message
	
	sealed trait NativeSendRequest [T <: BaseRequest[T, R], R <: BaseResponse] {
		def request: T
	}
	
	case class NativeSimpleSendRequest [REQ <: AbstractSendRequest[REQ]] (
		override val request: REQ
	) extends NativeSendRequest[REQ, SendResponse]
	
	case class NativeMultipartSendRequest (
		override val request: SendMediaGroup
	) extends NativeSendRequest[SendMediaGroup, MessagesResponse]
	
}
