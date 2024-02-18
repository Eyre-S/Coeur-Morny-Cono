package cc.sukazyo.cono.morny.core.bot.api.messages

import cc.sukazyo.cono.morny.core.bot.api.messages.MessageThread.{CallbackParameterized, ThreadKey}
import cc.sukazyo.cono.morny.util.tgapi.Standardize.{ChatID, UserID}
import cc.sukazyo.cono.morny.util.EpochDateTime.DurationMillis
import com.pengrad.telegrambot.model.Message

trait MessageThread [P] {
	
	val starterContext: MessagingContext.WithUserAndMessage
	lazy val threadKey: ThreadKey = ThreadKey `fromContext` starterContext
	val passingData: P
	val callback: CallbackParameterized[P]
	val timeout: DurationMillis = 5 * 60 * 1000
	
	def continueThread (continuingMessage: Message): Unit = {
		callback.callback(continuingMessage, starterContext, passingData)
	}
	
}

object MessageThread {
	
	@FunctionalInterface
	trait Callback:
		def callback(message: Message, previousContext: MessagingContext.WithUserAndMessage): Any
	@FunctionalInterface
	trait CallbackParameterized [P]:
		def callback(message: Message, previousContext: MessagingContext.WithUserAndMessage, passingContext: P): Any
	given Conversion[Callback, CallbackParameterized[Unit]] =
		(callback: Callback) => (message, previousContext, _) => callback.callback(message, previousContext)
	
	def apply [P]
	(using _cxt: MessagingContext.WithUserAndMessage)
	(_data: P)
	(_callback: CallbackParameterized[P])
	: MessageThread[P] = new MessageThread[P] {
		override val starterContext: MessagingContext.WithUserAndMessage = _cxt
		override val passingData: P = _data
		override val callback: CallbackParameterized[P] = _callback
	}
	
	def apply
	(using _cxt: MessagingContext.WithUserAndMessage)
	(_callback: Callback)
	: MessageThread[Unit] = new MessageThread[Unit] {
		override val starterContext: MessagingContext.WithUserAndMessage = _cxt
		override val passingData: Unit = ()
		override val callback: CallbackParameterized[Unit] = _callback
	}
	
	case class ThreadKey (chatid: ChatID, userid: UserID)
	object ThreadKey:
		infix def fromMessage (message: Message): ThreadKey =
			ThreadKey(message.chat().id(), message.from().id())
		infix def fromContext (cxt: MessagingContext.WithUser): ThreadKey =
			ThreadKey(cxt.bind_chat.id(), cxt.bind_user.id())
	
}
