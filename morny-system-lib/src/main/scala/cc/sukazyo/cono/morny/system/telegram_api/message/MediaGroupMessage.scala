package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeMultipartSendRequest
import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import cc.sukazyo.cono.morny.system.telegram_api.objects.AbstractClientMedia
import com.pengrad.telegrambot.model.request.{InputMedia, ReplyParameters}
import com.pengrad.telegrambot.request.SendMediaGroup
import com.pengrad.telegrambot.response.MessagesResponse

trait MediaGroupMessage
	extends MediaMessage [NativeMultipartSendRequest, SendMediaGroup, MessagesResponse] {
	
	def medias: List[AbstractClientMedia[?]]
	
	def toInputMedias: List[InputMedia[?]] =
		medias.map(_.toNative)
	
}

object MediaGroupMessage {
	
	class ClientImpl (
		
		override val chat: Chat,
		override val replyParameters: Option[ReplyParameters],
		
		override val medias: List[AbstractClientMedia[?]],
		
	) extends MediaGroupMessage {
		
		override def getSendRequest (sendContext: SendMessageContext): NativeMultipartSendRequest = {
			val request = SendMediaGroup(
				this.chat.id,
				this.toInputMedias*
			)
			NativeMultipartSendRequest(request)
		}
		
	}
	
	trait CreateOps {
		this: Message =>
		
		def media (media: AbstractClientMedia[?], medias: AbstractClientMedia[?]*): MediaGroupMessage =
			new ClientImpl(
				chat = this.chat,
				replyParameters = this.replyParameters,
				medias = media :: medias.toList
			)
		
	}
	
}
