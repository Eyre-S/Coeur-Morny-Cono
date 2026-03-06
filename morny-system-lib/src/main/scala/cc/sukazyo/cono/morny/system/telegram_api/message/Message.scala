package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.{NativeMultipartSendRequest, NativeSendRequest, NativeSimpleSendRequest}
import cc.sukazyo.cono.morny.system.telegram_api.chat.{Chat, ThreadableChat}
import com.pengrad.telegrambot.model.request.ReplyParameters

trait Message extends MaybeSendableMessage {
	
	def chat: Chat
	
	def replyParameters: Option[ReplyParameters]
	
	//	def replyMarkup: Null
	//	def inlineKeyboard: this.replyMarkup.type = replyMarkup
	
	override def decorateSendRequest (request: NativeSendRequest[?, ?]): Unit = {
		
		request match {
			case simple: NativeSimpleSendRequest[_] =>
				
				this.chat match
					case chatThread: ThreadableChat =>
						simple.request.messageThreadId(chatThread.threadId)
				
				this.replyParameters.map(simple.request.replyParameters)
				
			case multipart: NativeMultipartSendRequest =>
				
				this.chat match
					case chatThread: ThreadableChat =>
						multipart.request.messageThreadId(chatThread.threadId)
				
				this.replyParameters.map(multipart.request.replyParameters)
				
		}
		
	}
	
}
