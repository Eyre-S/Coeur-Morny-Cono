package cc.sukazyo.cono.morny.util.tgapi

import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.request.{BaseRequest, GetChatMember}
import com.pengrad.telegrambot.response.BaseResponse

import scala.annotation.targetName

object TelegramExtensions {
	
	object Bot { extension (bot: TelegramBot) {
		
		@throws[EventRuntimeException]
		def exec [T <: BaseRequest[T, R], R <: BaseResponse] (request: T, onError_message: String = ""): R = {
			try {
				val response = bot execute request
				if response isOk then return response
				throw EventRuntimeException.ActionFailed(
					if onError_message isEmpty then response.errorCode toString else onError_message,
					response
				)
			} catch
				case e: EventRuntimeException.ActionFailed => throw e
				case e: RuntimeException =>
					throw EventRuntimeException.ClientFailed(e)
		}
		
	}}
	
	object Update { extension (update: Update) {
		
		def extractSourceChat: Option[Chat] =
			if      (update.message != null)            Some(update.message.chat)
			else if (update.editedMessage != null)      Some(update.editedMessage.chat)
			else if (update.channelPost != null)        Some(update.channelPost.chat)
			else if (update.editedChannelPost != null)  Some(update.editedChannelPost.chat)
			else if (update.inlineQuery != null)        None
			else if (update.chosenInlineResult != null) None
			else if (update.callbackQuery != null)      Some(update.callbackQuery.message.chat)
			else if (update.shippingQuery != null)      None
			else if (update.preCheckoutQuery != null)   None
			else if (update.poll != null)               None
			else if (update.pollAnswer != null)         None
			else if (update.myChatMember != null)       Some(update.myChatMember.chat)
			else if (update.chatMember != null)         Some(update.chatMember.chat)
			else if (update.chatJoinRequest != null)    Some(update.chatJoinRequest.chat)
			else None
		
		def extractSourceUser: Option[User] =
			if      (update.message != null)            Some(update.message.from)
			else if (update.editedMessage != null)      Some(update.editedMessage.from)
			else if (update.channelPost != null)        None
			else if (update.editedChannelPost != null)  None
			else if (update.inlineQuery != null)        Some(update.inlineQuery.from)
			else if (update.chosenInlineResult != null) Some(update.chosenInlineResult.from)
			else if (update.callbackQuery != null)      Some(update.callbackQuery.from)
			else if (update.shippingQuery != null)      Some(update.shippingQuery.from)
			else if (update.preCheckoutQuery != null)   Some(update.preCheckoutQuery.from)
			else if (update.poll != null)               None
			else if (update.pollAnswer != null)         Some(update.pollAnswer.user)
			else if (update.myChatMember != null)       Some(update.myChatMember.from)
			else if (update.chatMember != null)         Some(update.chatMember.from)
			else if (update.chatJoinRequest != null)    Some(update.chatJoinRequest.from)
			else None
		
	}}
	
	object Chat { extension (chat: Chat) {
		
		def hasMember (user: User) (using TelegramBot): Boolean =
			memberHasPermission(user, ChatMember.Status.member)
		
		def memberHasPermission (user: User, permission: ChatMember.Status) (using bot: TelegramBot): Boolean = {
			
			//noinspection ScalaUnusedSymbol
			enum UserPermissionLevel(val level: Int):
				private case CREATOR extends UserPermissionLevel(10)
				private case ADMINISTRATOR extends UserPermissionLevel(3)
				private case MEMBER extends UserPermissionLevel(1)
				private case RESTRICTED extends UserPermissionLevel(-1)
				private case LEFT extends UserPermissionLevel(-3)
				private case KICKED extends UserPermissionLevel(-5)
				@targetName("equalOrGreaterThan")
				def >= (another: UserPermissionLevel): Boolean = this.level >= another.level
			object UserPermissionLevel:
				def apply(status: ChatMember.Status): UserPermissionLevel =
					status match
						case ChatMember.Status.creator => CREATOR
						case ChatMember.Status.administrator => ADMINISTRATOR
						case ChatMember.Status.member => MEMBER
						case ChatMember.Status.restricted => RESTRICTED
						case ChatMember.Status.left => LEFT
						case ChatMember.Status.kicked => KICKED
				def apply (chatMember: ChatMember): UserPermissionLevel = apply(chatMember.status)
			
			import Bot.*
			val chatMember: ChatMember = (bot exec GetChatMember(chat.id, user.id)).chatMember
			if chatMember eq null then false
			else UserPermissionLevel(chatMember) >= UserPermissionLevel(permission)
			
		}
		
	}}
	
	object Message { extension (self: Message) {
		
		def entitiesSafe: List[MessageEntity] =
			if self.entities == null then Nil else
				self.entities.toList
		
		def textWithUrls: String =
			(self.text :: self.entitiesSafe.map(_.url).filterNot(_ == null))
				.mkString(" ")
		
	}}
	
	class LimboUser (id: Long) extends User(id)
	class LimboChat (val _id: Long) extends Chat() {
		override val id: java.lang.Long = _id
	}
 
}
