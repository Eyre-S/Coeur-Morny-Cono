package cc.sukazyo.cono.morny.util.tgapi

import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.{Chat, ChatMember, User}
import com.pengrad.telegrambot.request.{BaseRequest, GetChatMember}
import com.pengrad.telegrambot.response.BaseResponse

import scala.annotation.targetName

object TelegramExtensions {
	
	object Bot { extension (bot: TelegramBot) {
		
		def exec [T <: BaseRequest[T, R], R <: BaseResponse] (request: T, onError_message: String = ""): R = {
			val response = bot execute request
			if response isOk then return response
			throw EventRuntimeException.ActionFailed(
				if onError_message isEmpty then response.errorCode toString else onError_message,
				response
			)
		}
		
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
	
	class LimboUser (id: Long) extends User(id)
	class LimboChat (val _id: Long) extends Chat() {
		override val id: java.lang.Long = _id
	}
 
}
