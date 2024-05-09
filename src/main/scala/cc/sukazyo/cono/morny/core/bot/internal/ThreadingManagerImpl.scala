package cc.sukazyo.cono.morny.core.bot.internal

import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener, ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.core.bot.api.messages.{MessageThread, MessagingContext, ThreadingManager}
import cc.sukazyo.cono.morny.core.bot.api.messages.MessageThread.{Callback, CallbackParameterized, ThreadKey}
import cc.sukazyo.cono.morny.util.schedule.{DelayedTask, Scheduler, Task}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.{Message, Update}
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.SendMessage

class ThreadingManagerImpl (using bot: TelegramBot) extends ThreadingManager {
	
	private val threadMap = collection.mutable.Map[ThreadKey, InternalMessageThread[?]]()
	private val threadMapCleaner = Scheduler(isDaemon = true)
	
	private class InternalMessageThread [P] (
		val thread: MessageThread[P]
	) {
		
		val timeoutCleanerTask: Task = ThreadingCleanerTask(this)
		
		def onExecuteIt (message: Message): Boolean =
			val succeed = threadMap.synchronized:
				this.onCancelIt()
			if succeed then
				this.thread.continueThread(message)
			succeed
		
		def onCancelIt (): Boolean =
			threadMap.synchronized:
				threadMapCleaner % timeoutCleanerTask
				threadMap.remove(thread.threadKey).nonEmpty
		
	}
	private def ThreadingCleanerTask [P] (iThread: InternalMessageThread[P]): Task =
		DelayedTask(s"", iThread.thread.timeout, {
			threadMap.synchronized:
				SendMessage(
					iThread.thread.threadKey.chatid,
					s"Timeout for future messages."
				).unsafeExecute
				threadMap -= iThread.thread.threadKey
		})
	
	private def registerThread [P] (thread: MessageThread[P]): Unit = {
		threadMap.synchronized:
			if this.cancelThread(thread.threadKey) then
				SendMessage(
					thread.threadKey.chatid,
					"""There seems another message thread is waiting for future messages.
					  |That thread has been canceled automatically.
					  |""".stripMargin
				).unsafeExecute
			val iThread = InternalMessageThread[P](thread)
			threadMapCleaner ++ iThread.timeoutCleanerTask
			threadMap += (thread.threadKey -> iThread)
	}
	
	override def doAfter
	(using _cxt: MessagingContext.WithUserAndMessage)
	(_callback: Callback)
	: Unit =
		registerThread(MessageThread(_callback))
	
	override def doAfter[P]
	(using _cxt: MessagingContext.WithUserAndMessage)
	(_data: P)
	(_callback: CallbackParameterized[P])
	: Unit =
		registerThread(MessageThread(_data)(_callback))
	
	override def doAfter[P] (thread: MessageThread[P]): Unit =
		registerThread(thread)
	
	override def tryUpdate (message: Message): Boolean =
		threadMap.get(ThreadKey fromMessage message)
			.exists(_.onExecuteIt(message))
	
	/** Ensure current context have no ongoing message thread so that you can create a new one
	  * safely.
	  *
	  * If there's already an ongoing message thread, it will be canceled, and will output a
	  * notice message to that context.
	  *
	  * @see [[cancelThread]] the method that will be used to cancel the ongoing message thread.
	  *
	  * @param _cxt the current context you want to check. This context will be converted to the
	  *             [[ThreadKey]] in order to do the check, if needed, some message will also
	  *             sent to this context.
	  */
	override def ensureCleanState (using _cxt: MessagingContext.WithUserAndMessage): Unit =
		if cancelThread(ThreadKey fromContext _cxt) then
			SendMessage(
				_cxt.bind_chat.id,
				"""There seems another message thread is waiting for future messages.
				  |That thread has been canceled automatically.
				  |""".stripMargin
			).replyToMessageId(_cxt.bind_message.messageId)
				.unsafeExecute
	
	override def cancelThread (threadKey: ThreadKey): Boolean =
		threadMap.get(threadKey)
			.exists(_.onCancelIt())
	
	object NextMessageCatcher extends EventListener {
		
		override def onMessage (using event: EventEnv): Unit = {
			if tryUpdate(event.update.message) then
				event.setEventOk
		}
		
	}
	
	object CancelCommand extends ISimpleCommand {
		
		override val name: String = "cancel"
		override val aliases: List[ICommandAlias] = Nil
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			if cancelThread(ThreadKey fromMessage event.message) then
				SendMessage(
					event.message.chat.id,
					"Canceled."
				).replyToMessageId(event.message.messageId).unsafeExecute
			else
				SendMessage(
					event.message.chat.id,
					"No active message thread to cancel."
				).replyToMessageId(event.message.messageId).unsafeExecute
		}
		
	}
	
}
