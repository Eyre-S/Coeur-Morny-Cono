package cc.sukazyo.cono.morny.util.tgapi

import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import cc.sukazyo.cono.morny.util.EpochDateTime.{EpochMillis, EpochSeconds}
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.request.{BaseRequest, GetChatMember}
import com.pengrad.telegrambot.response.BaseResponse

import scala.annotation.targetName

object TelegramExtensions {
	
	object Bot { extension (bot: TelegramBot) {
		
		/** Try sync execute a [[BaseRequest request]], and throws [[EventRuntimeException]]
		  * when it fails.
		  *
		  * It will use [[Telegram.execute]] to execute the request, and check if the response is failed.
		  *
		  * If the request returned a [[BaseResponse response]], the [[BaseResponse.isOk]] will be checked.
		  * if it is false, the method will ended with a [[EventRuntimeException.ActionFailed]], which
		  * message is param [[onError_message]](or [[BaseResponse.errorCode]] if it is empty).
		  *
		  * If the request failed in the client (that means [[TelegramBot.execute]] call failed with Exceptions),
		  * this method will ended with a [[EventRuntimeException.ClientFailed]].
		  *
		  * @param request The request needed to be run.
		  * @param onError_message The exception message that will be thrown when the [[request]]'s
		  *                        [[BaseResponse response]] is not ok([[BaseResponse.isOk isOk()]] == false)
		  * @tparam T Type of the request
		  * @tparam R Type of the response that request should returns.
		  * @throws EventRuntimeException Whenever the request's response is not ok, or the request does not
		  *                               return a response. See above for more info.
		  * @return The succeed response (which is returned by [[TelegramBot.execute]]) as is.
		  *
		  * @since 1.0.0
		  */
		@throws[EventRuntimeException]
		def exec [T <: BaseRequest[T, R], R <: BaseResponse] (request: BaseRequest[T, R], onError_message: String = ""): R = {
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
		
		/** Get the [[Chat]] that the update comes from.
		  *
		  * For the update types that is non-chat related, or the update types that framework does not
		  * supported yet, this method will return [[None]].
		  *
		  * ### Supported Update Types
		  *
		  * Belows are the supported update types that will return a valid [[Chat]].
		  *
		  *  - [[Update.message]]: Chat that the message belongs.
		  *  - [[Update.editedMessage]]: Chat that the edited message belongs.
		  *  - [[Update.channelPost]]: Chat that the channel post message belongs.
		  *  - [[Update.editedChannelPost]]: Chat that the edited channel post message belongs.
		  *  - [[Update.callbackQuery]]: Chat that the callback query's source message (a callback query
		  *    is triggered from a inline message button, that inline message is the source message) belongs.
		  *  - [[Update.myChatMember]]: Chat that where member status of current bot changed there.
		  *  - [[Update.chatMember]]: Chat that any one user's member status changed there.
		  *  - [[Update.chatJoinRequest]]: Chat that the join request comes from.
		  *
		  * Belows are the known update types that is not chat related, so there's only a [[None]] returned.
		  *
		  *  - [[Update.inlineQuery]]
		  *  - [[Update.chosenInlineResult]]
		  *  - [[Update.shippingQuery]]
		  *  - [[Update.preCheckoutQuery]]
		  *  - [[Update.poll]]
		  *  - [[Update.pollAnswer]]
		  *
		  * Supported up to Telegram Bot API 6.2
		  *
		  * @return An [[Option]] either contains a [[Chat]] that the update comes from, or [[None]] if there's
		  *         no user or update type is unsupported.
		  * @since 2.0.0
		  */
		def sourceChat: Option[Chat] =
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
		
		/** Get the [[User]] that the update comes from.
		  *
		  * For the update types that is non-user related, or the update types that framework does not
		  * supported yet, this method will return [[None]].
		  *
		  * ### Supported Update Types
		  *
		  * Belows are the supported update types that will return a valid [[User]].
		  *
		  *  - [[Update.message]]: User that sent the message.
		  *  - [[Update.editedMessage]]: User that sent the message. Notice that is the user who ORIGINAL
		  *    SENT the message but NOT EDITED the message due to the API limitation, while in current Bot
		  *    API version (v7.0), editing other's message is not allowed so it should have no problem.
		  *  - [[Update.inlineQuery]]: User that executing the inline query.
		  *  - [[Update.chosenInlineResult]]: User that chosen the inline result of a inline query.
		  *  - [[Update.callbackQuery]]: User that triggered the callback query.
		  *  - [[Update.shippingQuery]]: User that sent the shipping query.
		  *  - [[Update.preCheckoutQuery]]: User that sent the pre-checkout query.
		  *  - [[Update.pollAnswer]]: User that answered a un-anonymous poll.
		  *  - [[Update.myChatMember]]: Current bot that my member status changed.
		  *  - [[Update.chatMember]]: User that their member status changed.
		  *  - [[Update.chatJoinRequest]]: User that sent a join request.
		  *
		  * Belows are the known update types that is not user related, so there's only a [[None]] returned.
		  *
		  *  - [[Update.channelPost]]
		  *  - [[Update.editedChannelPost]]
		  *  - [[Update.poll]] <small>(odd, but it is no sender in the latest Bot API 7.0)</small>
		  *
		  * Supported up to Telegram Bot API 6.2
		  *
		  * @return An [[Option]] either contains a [[User]] that the update comes from, or [[None]] if there's
		  *         no user or update type is unsupported.
		  * @since 2.0.0
		  */
		def sourceUser: Option[User] =
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
		
		/** Date time of the update.
		  *
		  * This date-time is guessed from the update content -- it may not response the accurate or even real
		  * time when the update happens. Some update types contains no related time information, so it can only
		  * returns [[None]].
		  *
		  * Telegram uses UNIX seconds as the time unit, and Java Telegram Bot API (which this project
		  * dependents this) use [[Int]] to store it. so the returned time will be formatted to [[EpochSeconds]]
		  * which also uses UNIX seconds as the time unit, and stored in type [[Int]].
		  *
		  * ### Supported Update Types
		  *
		  * Belows are the supported update types that will return a valid time.
		  *
		  *  - [[Update.message]]: Time that the message is sent. Should be fixed and accurate.
		  *  - [[Update.editedMessage]]: Time that the message is edited. Can be changed when the message is
		  *    edited again, but should be the fixed editing time in the specific update.
		  *  - [[Update.channelPost]]: Time that the channel post message is sent. Should be fixed and accurate.
		  *  - [[Update.editedChannelPost]]: Time that the channel post message is edited. Same with
		  *    [[Update.editedMessage]]
		  *  - [[Update.myChatMember]]: Time that the member status of current bot changes done.
		  *  - [[Update.chatMember]]: Time that one user's member status changes done.
		  *  - [[Update.chatJoinRequest]]: Time that the join request is sent.
		  *
		  * Belows have no any time information, so there's only a [[None]] returned.
		  *
		  *  - [[Update.inlineQuery]]
		  *  - [[Update.chosenInlineResult]]
		  *  - [[Update.callbackQuery]]
		  *  - [[Update.shippingQuery]]
		  *  - [[Update.preCheckoutQuery]]
		  *  - [[Update.poll]]
		  *  - [[Update.pollAnswer]]
		  *
		  * Supported up to Telegram Bot API 6.2
		  *
		  * @return An [[Option]] either contains a [[EpochSeconds]] that may be the time when update happens,
		  *         or [[None]] if there's unsupported.
		  * @since 2.0.0
		  */
		def sourceTime: Option[EpochSeconds] =
			if      (update.message != null)            Some(update.message.date)
			else if (update.editedMessage != null)      Some(update.editedMessage.editDate)
			else if (update.channelPost != null)        Some(update.channelPost.date)
			else if (update.editedChannelPost != null)  Some(update.editedChannelPost.editDate)
			else if (update.inlineQuery != null)        None
			else if (update.chosenInlineResult != null) None
			else if (update.callbackQuery != null)      None
			else if (update.shippingQuery != null)      None
			else if (update.preCheckoutQuery != null)   None
			else if (update.poll != null)               None
			else if (update.pollAnswer != null)         None
			else if (update.myChatMember != null)       Some(update.myChatMember.date)
			else if (update.chatMember != null)         Some(update.chatMember.date)
			else if (update.chatJoinRequest != null)    Some(update.chatJoinRequest.date)
			else None
		
	}}
	
	object Chat { extension (chat: Chat) {
		
		/** Check if a user is a member of this chat.
		  *
		  * It is equivalent to `memberHasPermission(user, ChatMember.Status.member)`.
		  *
		  * It checks if the user's member status is [[ChatMember.Status.member]], so if the member is in
		  * this chat but they are been [[ChatMember.Status.restricted]], it will be treated as *not a member*.
		  * 
		  * It needs to execute a request getting chat member, so it requires a live [[TelegramBot]] instance,
		  * and the bot needs to be a member of this chat so that it can read the chat member.
		  *
		  * **Notice:** This method will execute a request to the Telegram Bot API, so it may be slow.
		  *
		  * @see [[memberHasPermission]]
		  *
		  * @param user The user that want to check if they are a member of this chat.
		  * @return [[true]] when the user is a member of this chat, [[false]] otherwise.
		  *
		  * @since 1.0.0
		  */
		def hasMember (user: User) (using TelegramBot): Boolean =
			memberHasPermission(user, ChatMember.Status.member)
		
		/** Check if a user has a permission level in this chat.
		  * 
		  * It checks if the user's member status is equal or greater than the required permission level.
		  * 
		  * Due to API does not implemented a permission level system, so inside this method, there's a
		  * permission level table:
		  * 
		  * | [[ChatMember.Status]] | Permission Level |
		  * |----------------------|-----------------:|
		  * | [[ChatMember.Status.creator]] | 10 |
		  * | [[ChatMember.Status.administrator]] | 3 |
		  * | [[ChatMember.Status.member]] | 1 |
		  * | [[ChatMember.Status.restricted]] | -1 |
		  * | [[ChatMember.Status.left]] | -3 |
		  * | [[ChatMember.Status.kicked]] | -5 |
		  *
		  * **Notice:** This method will execute a request to the Telegram Bot API, so it may be slow.
		  * 
		  * @param user The user that wanted to check if they have the permission.
		  * @param permission The required permission level.
		  * @param bot A live [[TelegramBot]] that will be used to execute the getChatMember request. It
		  *            should be a member of this chat so that can read the chat member for this method works.
		  * @return [[true]] if the user have the permission or higher permission, [[false]] otherwise.
		  */
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
