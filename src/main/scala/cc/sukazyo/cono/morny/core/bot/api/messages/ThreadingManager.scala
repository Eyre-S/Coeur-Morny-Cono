package cc.sukazyo.cono.morny.core.bot.api.messages

import cc.sukazyo.cono.morny.core.bot.api.messages.MessageThread.{Callback, CallbackParameterized, ThreadKey}
import com.pengrad.telegrambot.model.Message

/** Message threads controller.
  */
trait ThreadingManager {
	
	/** Do the `_callback` when the next message is arrived.
	  *
	  * @since 2.0.0
	  *
	  * @param _cxt Current message event context.
	  * @param _callback Function that will be executed in the next message.
	  */
	def doAfter
	(using _cxt: MessagingContext.WithUserAndMessage)
	(_callback: Callback)
	: Unit
	
	/** Do the `_callback` when the next message is arrived.
	  * 
	  * @since 2.0.0
	  * 
	  * @param _cxt Current message event context.
	  * @param _data Data that will passing to the `_callback` from current context.
	  * @param _callback The callback function that will be executed in the next message.
	  * @tparam P Type of the passing `_data`.
	  */
	def doAfter[P]
	(using _cxt: MessagingContext.WithUserAndMessage)
	(_data: P)
	(_callback: CallbackParameterized[P])
	: Unit
	
	/**
	  * @since 2.0.0
	  */
	def doAfter[P] (thread: MessageThread[P]): Unit
	
	/** Try to continue run a message thread using the given message.
	  * 
	  * @since 2.0.0
	  * 
	  * @param message The message that will be used to continue the thread.
	  * @return `true` if any one message thread is successfully continued, `false` if this given
	  *         message cannot continue any one message thread.
	  */
	def tryUpdate (message: Message): Boolean
	
	/** Cancel a message thread.
	  * 
	  * @since 2.0.0
	  * 
	  * @param threadKey The key of the message thread.
	  * @return `true` if there's any one message thread is canceled, `false` if there's no message
	  *         thread associated with the given key.
	  */
	def cancelThread (threadKey: ThreadKey): Boolean
	
}
