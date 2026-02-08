package cc.sukazyo.cono.morny.system.telegram_api.action

import cc.sukazyo.cono.morny.system.telegram_api.account.BotAccount
import cc.sukazyo.cono.morny.system.telegram_api.chat.ChatChannel
import cc.sukazyo.cono.morny.system.telegram_api.message.{SendableMessage, TextMessage, UnsupportedForSendException}
import cc.sukazyo.cono.morny.system.telegram_api.text.Text
import com.pengrad.telegrambot.request.{AbstractSendRequest, SendMessage}
import com.pengrad.telegrambot.response.SendResponse

trait TelegramActions {
	this: BotAccount =>
	
	@throws[ClientRequestException]
	@throws[UnsupportedForSendException]
	def sendMessage (message: SendableMessage, chat: ChatChannel): SendResponse =  {
		
		val sendRequestDecorator = (request: AbstractSendRequest[?]) => {
			if (message.replyParameters != null)
				request.replyParameters(message.replyParameters)
		}
		
		message match {
			
			case textMessage: TextMessage =>
				
				val text: Text.CompiledText = textMessage.text.compile
				val sendRequest = SendMessage(
					chat.id,
					text.text
				).entities(text.entities*)
				
				sendRequestDecorator(sendRequest)
				
				this.exec(sendRequest)
				
			case _ =>
				throw UnsupportedForSendException();
			
		}
		
	}
	
}
