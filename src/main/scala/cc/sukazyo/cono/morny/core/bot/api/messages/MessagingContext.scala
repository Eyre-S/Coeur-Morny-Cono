package cc.sukazyo.cono.morny.core.bot.api.messages

import com.pengrad.telegrambot.model.{Chat, Message, User}

/**
  * @since 2.0.0
  */
trait MessagingContext:
	val bind_chat: Chat

/**
  * @since 2.0.0
  */
object MessagingContext {
	
	given String = "aaa"
	
	def apply (_chat: Chat): MessagingContext =
		new MessagingContext:
			override val bind_chat: Chat = _chat
	trait WithUser extends MessagingContext:
		val bind_user: User
	def apply (_chat: Chat, _user: User): WithUser =
		new WithUser:
			override val bind_chat: Chat = _chat
			override val bind_user: User = _user
	trait WithMessage extends MessagingContext:
		val bind_message: Message
	def apply (_chat: Chat, _message: Message): WithMessage =
		new WithMessage:
			override val bind_chat: Chat = _chat
			override val bind_message: Message = _message
	trait WithUserAndMessage extends MessagingContext with WithMessage with WithUser
	def apply (_chat: Chat, _user: User, _message: Message): WithUserAndMessage =
		new WithUserAndMessage:
			override val bind_chat: Chat = _chat
			override val bind_user: User = _user
			override val bind_message: Message = _message
	
	/** Extract a message context from a message (or message event).
	  * 
	  * @param message The message.
	  * @return The message context, contains the message's belongs chat, sender user and message itself.
	  */
	def extract (using message: Message): WithUserAndMessage =
		apply(message.chat, message.from, message)
	
}
