package cc.sukazyo.cono.morny.system.telegram_api

import com.pengrad.telegrambot.model.{Chat, Message, User}
import com.pengrad.telegrambot.request.{AbstractSendRequest, BaseRequest, SendMediaGroup}
import com.pengrad.telegrambot.response.{BaseResponse, MessagesResponse, SendResponse}

object Natives {
	
	/** Native chat type of Telegram API
	  * @since 2.0.0-alpha22
	  */
	type NativeChat = Chat
	/** Native user type of Telegram API
	  * @since 2.0.0-alpha22
	  */
	type NativeUser = User
	/** Native message type of Telegram API.
	  * @since 2.0.0-alpha22
	  */
	type NativeMessage = Message
	
	/** Abstract uniform layer for send request.
	  *
	  * ## Why this trait is needed?
	  *
	  * Telegram Bot API has two different types to send a message(or multiple messages,
	  * exactly):
	  *
	  * - [[AbstractSendRequest]]: any message that has only one [[cc.sukazyo.cono.morny.system.telegram_api.Standardize.MessageID]]
	  *   and does not introduce [[cc.sukazyo.cono.morny.system.telegram_api.Standardize.MessageGroupID]]
	  *   is sent via this type of request. Its corresponding response is [[SendResponse]],
	  *   which contains only one [[NativeMessage]].
	  * - [[SendMediaGroup]]: actually for [[SendMediaGroup]] only. It sends multiple messages
	  *   into one message group. Since there are multiple [[NativeMessage]] underlay, the
	  *   response is [[MessagesResponse]] instead.
	  *
	  * So that this trait is designed to unify these two types of send request into one type,
	  * so that the associated send method can be implemented in a more uniform way.
	  *
	  * ## Types under this trait
	  *
	  * This trait is a sealed trait, since the decoration requires to match the exact type of
	  * the send request.
	  *
	  * There are two subtypes of this trait:
	  *
	  * - [[NativeSimpleSendRequest]]: for the send method [[AbstractSendRequest]]. Its
	  *   response is [[SendResponse]].
	  *   Note that the exact send request type may still variant.
	  * - [[NativeMultipartSendRequest]]: for the send method [[SendMediaGroup]]. Its response
	  *   is [[MessagesResponse]].
	  *
	  * @tparam T Send request type.
	  * @tparam R Response type of the send request.
	  * @since 2.0.0-alpha22
	  */
	sealed trait NativeSendRequest [T <: BaseRequest[T, R], R <: BaseResponse] {
		def request: T
	}
	
	/** @see [[NativeSendRequest]]
	  * @since 2.0.0-alpha22
	  */
	case class NativeSimpleSendRequest [REQ <: AbstractSendRequest[REQ]] (
		override val request: REQ
	) extends NativeSendRequest[REQ, SendResponse]
	
	case class NativeMultipartSendRequest (
		override val request: SendMediaGroup
	) extends NativeSendRequest[SendMediaGroup, MessagesResponse]
	
}
