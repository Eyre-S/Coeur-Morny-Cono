package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.action.SendMessageContext
import cc.sukazyo.cono.morny.system.telegram_api.text.Text
import com.pengrad.telegrambot.request.{AbstractSendRequest, SendMessage}

trait TextMessage (
	
	val text: Text
	
) extends Message with SendableMessage[SendMessage] {
	
	override def getSendRequest (sendContext: SendMessageContext): AbstractSendRequest[SendMessage] = {
		val text = this.text.compile
		val request = SendMessage(
			this.chat.id,
			text.text,
		)
		if (text.entities.nonEmpty)
			request.entities(text.entities*)
		request
	}
	
}
