package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.util.EpochDateTime.EpochSeconds
import com.pengrad.telegrambot.model.{Chat, ChatMemberUpdated, Message, User}

case class EventContext (
	message: Option[Message] = None,
	chat: Option[Chat] = None,
	invoker: Option[User] = None,
	textInput: Option[String] = None,
	timestamp: Option[EpochSeconds] = None
)

object EventContext {
	
	/** Extract the EventContext from a message.
	  * 
	  * The [[EventContext.message]], [[EventContext.invoker]], [[EventContext.textInput]], [[EventContext.timestamp]]
	  * will be written.
	  * 
	  * This will automatically detect if the message is edited, and use the editDate if is.
	  * 
	  * If the message is a forwarded message, this should use the forwarded message but not the original message's
	  * context, depends on the Telegram API's behavior. This may have issues due to I may not understand the API spec
	  * clearly.
	  */
	def fromMessage (message: Message): EventContext =
		EventContext(
			message = Some(message),
			chat = Some(message.chat),
			invoker = Some(message.from),
			textInput = Some(message.text),
			timestamp = Some(
				if (message.editDate != null) message.editDate
				else message.date
			)
		)
	
	def fromChatMemberUpdated (chatMemberUpdated: ChatMemberUpdated): EventContext =
		EventContext(
			invoker = Some(chatMemberUpdated.from),
			chat = Some(chatMemberUpdated.chat),
			timestamp = Some(chatMemberUpdated.date)
		)
	
}
