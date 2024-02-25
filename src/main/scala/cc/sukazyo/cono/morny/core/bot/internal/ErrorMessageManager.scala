package cc.sukazyo.cono.morny.core.bot.internal

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.core.bot.api.messages.{ErrorMessage, MessagingContext}
import cc.sukazyo.cono.morny.util.schedule.{DelayedTask, Scheduler}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.util.EpochDateTime.DurationMillis
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.{AbstractSendRequest, SendMessage}

class ErrorMessageManager (using coeur: MornyCoeur) {
	given TelegramBot = coeur.account
	
	private val expiresDuration: DurationMillis = 1000 * 60 * 60 * 5 // 5 hours
	
	private val errorMessageMap = collection.mutable.Map[MessagingContext.WithMessage.Key, ErrorMessage[?, ?]]()
	private val errorMessageMapCleaner = Scheduler(isDaemon = true)
	private def putErrorMessage (key: MessagingContext.WithMessage.Key, message: ErrorMessage[?, ?]): Unit =
		errorMessageMap.synchronized: // remove an error message after the given expires duration.
			errorMessageMap += (key -> message)
			errorMessageMapCleaner ++ DelayedTask("", expiresDuration, {
				errorMessageMap.remove(key)
			})
	
	/** Send an [[ErrorMessage]] and add this error message to ErrorMessage stash.
	  *
	  * This will execute the SendRequest using the default send config.
	  *
	  * @since 2.0.0
	  */
	def sendErrorMessage (message: ErrorMessage[?, ?]): Unit =
		// todo: which should be sent using user config
		val useType = ErrorMessage.Types.Simple
		sendErrorMessage(message, useType, isNewMessage = true)
	
	/** Send an [[ErrorMessage]] and add it to the stash if it is new.
	  *
	  * @since 2.0.0
	  *
	  * @param message The [[ErrorMessage]] to be sent.
	  * @param useType Which type of the message should be sent.
	  * @param injectedSendContext A context that determine where and how the message should
	  *                            be sent. Currently only the message id in this context will
	  *                            be used to determine the reply to message.
	  *
	  *                            This is part of the API limitation, but due to in normal cases,
	  *                            the source error message must be in the same chat so it may
	  *                            make sense.
	  * @param isNewMessage Is this ErrorMessage is a new error message, otherwise it should be
	  *                     an existed error message that just calling this method because of
	  *                     need to be resent some messages.
	  *
	  *                     If this is new, the message will be added to the stash, if not, it
	  *                     should be comes from the stash and will not be added to the stash again.
	  *
	  *                     Default is false.
	  */
	def sendErrorMessage (
		message: ErrorMessage[?, ?],
		useType: ErrorMessage.Types,
		injectedSendContext: Option[MessagingContext.WithMessage] = None,
		isNewMessage: Boolean = false
	): Unit = {
		val sendMessage = message
			.getByTypeNormal(useType)
			.asInstanceOf[AbstractSendRequest[Nothing]]
		injectedSendContext match
			case None =>
			case Some(cxt) =>
				sendMessage.replyToMessageId(cxt.bind_message.messageId)
		val response = sendMessage.unsafeExecute
		if isNewMessage then
			val key = MessagingContext.extract(using response.message).toChatMessageKey
			putErrorMessage(key, message)
	}
	
	/** Get the stashed [[ErrorMessage]] associated with the [[MessagingContext.WithMessage.Key message key]].
	  *
	  * If the message key does not associated with any error message in the stash, then
	  * [[None]] will be returned.
	  *
	  * Notice that one [[ErrorMessage]] will only be stashed for 5 hours then it will be
	  * cleaned up from the stash.
	  *
	  * @since 2.0.0
	  */
	def inspectMessage (messageKey: MessagingContext.WithMessage.Key): Option[ErrorMessage[?, ?]] =
		errorMessageMap.get(messageKey)
	
	object ShowErrorMessageCommand extends ISimpleCommand {
		override val name: String = "inspect"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			val cxt = MessagingContext.extract(using event.message)
			val cxtReplied = Option(event.message.replyToMessage).map(MessagingContext.extract(using _))
			errorMessageMap.get(cxtReplied.map(_.toChatMessageKey).orNull) match
				case Some(msg) =>
					sendErrorMessage(msg, ErrorMessage.Types.Complex, Some(cxt))
				case None =>
					SendMessage(
						cxt.bind_chat.id,
						"Not a error message."
					).replyToMessageId(cxt.bind_message.messageId)
						.unsafeExecute
	}
	
}
