package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSimpleSendRequest
import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.chat.Chat
import cc.sukazyo.cono.morny.system.telegram_api.text.{MessageText, NativeText}
import com.pengrad.telegrambot.model.request.ReplyParameters
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

trait TextMessage extends Message with SendableMessage[NativeSimpleSendRequest[SendMessage], SendMessage, SendResponse] {
	
	def text: MessageText
	
	override def getSendRequest (sendContext: SendMessageContext): NativeSimpleSendRequest[SendMessage] = {
		val text = this.text.compile
		val request = SendMessage(
			this.chat.id,
			text.message,
		)
		if (text.entities.nonEmpty)
			request.entities(text.entities*)
		text.parseMode.map(request.parseMode)
		NativeSimpleSendRequest(request)
	}
	
}

object TextMessage {
	
	class TextMessageImpl (
		
		override val chat: Chat,
		override val replyParameters: Option[ReplyParameters],
		
		override val text: MessageText
	
	) extends TextMessage
	
	trait CreateOps {
		this: Message =>
		
		def apply (text: MessageText): TextMessage =
			TextMessageImpl(this.chat, this.replyParameters, text)
		
		def apply (text: String): TextMessage =
			this.apply(NativeText.plain(text))
		
	}
	
}
