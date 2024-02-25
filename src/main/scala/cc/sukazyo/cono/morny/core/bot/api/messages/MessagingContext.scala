package cc.sukazyo.cono.morny.core.bot.api.messages

import cc.sukazyo.cono.morny.util.tgapi.Standardize.*
import com.pengrad.telegrambot.model.{Chat, Message, User}

/**
  * @since 2.0.0
  */
trait MessagingContext:
	val bind_chat: Chat
	def toChatKey: MessagingContext.Key =
		MessagingContext.Key(this)

/**
  * @since 2.0.0
  */
object MessagingContext {
	
	case class Key (chatId: ChatID)
	object Key:
		def apply (it: MessagingContext): Key = Key(it.bind_chat.id)
	def apply (_chat: Chat): MessagingContext =
		new MessagingContext:
			override val bind_chat: Chat = _chat
	
	trait WithUser extends MessagingContext:
		val bind_user: User
		def toChatUserKey: WithUser.Key =
			WithUser.Key(this)
	object WithUser:
		case class Key (chatID: ChatID, userId: UserID)
		object Key:
			def apply (it: WithUser): Key = Key(it.bind_chat.id, it.bind_user.id)
		def apply (_chat: Chat, _user: User): WithUser =
			new WithUser:
				override val bind_chat: Chat = _chat
				override val bind_user: User = _user
	def apply (_chat: Chat, _user: User): WithUser =
		WithUser(_chat, _user)
	
	trait WithMessage extends MessagingContext:
		val bind_message: Message
		def toChatMessageKey: WithMessage.Key =
			WithMessage.Key(this)
	object WithMessage:
		case class Key (chatId: ChatID, messageId: MessageID)
		object Key:
			def apply (it: WithMessage): Key = Key(it.bind_chat.id, it.bind_message.messageId)
		def apply (_chat: Chat, _message: Message): WithMessage =
			new WithMessage:
				override val bind_chat: Chat = _chat
				override val bind_message: Message = _message
	def apply (_chat: Chat, _message: Message): WithMessage =
		WithMessage(_chat, _message)
	
	trait WithUserAndMessage extends MessagingContext with WithMessage with WithUser:
		def toChatUserMessageKey: WithUserAndMessage.Key =
			WithUserAndMessage.Key(this)
	object WithUserAndMessage:
		case class Key (chatId: ChatID, userId: UserID, messageId: MessageID)
		object Key:
			def apply (it: WithUserAndMessage): Key = Key(it.bind_chat.id, it.bind_user.id, it.bind_message.messageId)
		def apply (_chat: Chat, _user: User, _message: Message): WithUserAndMessage =
			new WithUserAndMessage:
				override val bind_chat: Chat = _chat
				override val bind_user: User = _user
				override val bind_message: Message = _message
	def apply (_chat: Chat, _user: User, _message: Message): WithUserAndMessage =
		WithUserAndMessage(_chat, _user, _message)
	
	/** Extract a message context from a message (or message event).
	  * 
	  * @param message The message.
	  * @return The message context, contains the message's belongs chat, sender user and message itself.
	  */
	def extract (using message: Message): WithUserAndMessage =
		apply(message.chat, message.from, message)
	
}
