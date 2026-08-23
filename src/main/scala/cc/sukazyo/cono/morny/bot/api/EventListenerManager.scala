package cc.sukazyo.cono.morny.bot.api

import cc.sukazyo.cono.morny.{Log, MornyCoeur}
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Update.tryGetGroupId
import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.UpdatesListener

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

/** Contains a [[mutable.Queue]] of [[EventListener]], and delivery telegram [[Update]].
  *
  * Implemented [[process]] in [[UpdatesListener]] so it can directly used in [[com.pengrad.telegrambot.TelegramBot.setupListener]].
  *
  * @param coeur the [[MornyCoeur]] context.
  */
class EventListenerManager (using coeur: MornyCoeur) extends UpdatesListener {
	
	private val listeners = mutable.Queue.empty[EventListener]
	
	def register (listeners: EventListener*): Unit =
		this.listeners ++= listeners
	
	private class EventRunner (using updates: List[Update]) extends Thread {
		
		private val head = updates.head
		private val updId = s"upd-${head.updateId()}" + (
			if updates.length == 1 then ""
			else s"+${updates.length-1}")
		
		this setName s"$updId-nn"
		private var _currSubevent: String = "<not-running-yet>"
		private var _currListener: String = "<not-running-yet>"
		
		/** Current processing subevent */
		def currentSubevent: String = _currSubevent
		/** Current running listener */
		def currentListener: String = _currListener
		
		private def setRunnerStatus (subevent: String): Unit = {
			_currSubevent = subevent
			this setName s"$updId-$subevent"
		}
		
		private def setRunningListener (listener: EventListener): Unit =
			_currListener = listener.getClass.getName
		
		override def run (): Unit = {
			
			given env: EventEnv = EventEnv(head, updates)
			
			for (i <- listeners) {
				setRunningListener(i)
				if (i.executeFilter)
					runEventListener(i)
			}
			for (i <- listeners) {
				setRunningListener(i)
				runEventPost(i)
			}
			
		}
		
		private def runEventPost (i: EventListener)(using EventEnv): Unit = {
			setRunnerStatus("#post")
			i.atEventPost
		}
		
		private def runEventListener (i: EventListener)(using EventEnv): Unit = {
			try {
				setRunnerStatus(s"-universal")
				i.on
				setRunnerStatus("message")
				if head.message ne null then i.onMessage
				setRunnerStatus("edited-message")
				if head.editedMessage ne null then i.onEditedMessage
				setRunnerStatus("channel-post")
				if head.channelPost ne null then i.onChannelPost
				setRunnerStatus("edited-channel-post")
				if head.editedChannelPost ne null then i.onEditedChannelPost
				setRunnerStatus("inline-query")
				if head.inlineQuery ne null then i.onInlineQuery
				setRunnerStatus("chosen-inline-result")
				if head.chosenInlineResult ne null then i.onChosenInlineResult
				setRunnerStatus("callback-query")
				if head.callbackQuery ne null then i.onCallbackQuery
				setRunnerStatus("shipping-query")
				if head.shippingQuery ne null then i.onShippingQuery
				setRunnerStatus("pre-checkout-query")
				if head.preCheckoutQuery ne null then i.onPreCheckoutQuery
				setRunnerStatus("poll")
				if head.poll ne null then i.onPoll
				setRunnerStatus("poll-answer")
				if head.pollAnswer ne null then i.onPollAnswer
				setRunnerStatus("my-chat-member")
				if head.myChatMember ne null then i.onMyChatMemberUpdated
				setRunnerStatus("chat-member")
				if head.chatMember ne null then i.onChatMemberUpdated
				setRunnerStatus("chat-join-request")
				if head.chatJoinRequest ne null then i.onChatJoinRequest
			} catch case e => EventExceptionReporter.onException(e, this)
		}
		
	}
	
	
	import java.util
	import scala.jdk.CollectionConverters.*
	/** Delivery the telegram [[Update]]s to [[EventListener]]s that [[register]]ed.
	  *
	  * For normal updates, one update will deliver to one runner thread. For updates
	  * that belongs to one message group (media group), those who have the same group
	  * id will be packed to one list and deliver to one runner thread, so that
	  * [[EventListener]]s can receive a pack of updates that have full group messages.
	  *
	  * @return [[UpdatesListener.CONFIRMED_UPDATES_ALL]], for all Updates
	  *         should be processed in [[EventRunner]] created for it.
	  */
	override def process (updates: util.List[Update]): Int = {
		
		var i = 0
		while (i < updates.size) {
			
			val it = updates.get(i)
			val groupId = it.tryGetGroupId
			val cache: List[Update] = if (groupId.nonEmpty) {
				
				val buffer: ListBuffer[Update] = ListBuffer(it)
				while (i+1 < updates.size() && updates.get(i+1).tryGetGroupId == groupId) {
					i += 1
					buffer += updates.get(i)
				}
				buffer.toList
				
			} else List(it)
			EventRunner(using cache).start()
			
			i += 1
		}
		
		UpdatesListener.CONFIRMED_UPDATES_ALL
	}
	
	private object EventExceptionReporter {
		
		def onException (ex: Throwable, runner: EventRunner)(using env: EventEnv): Unit = {
			val errorMessage = StringBuilder()
			errorMessage ++= "Event throws unexpected exception!\n"
			errorMessage ++= s"current event_listener = ${runner.currentListener}\n"
			errorMessage ++= s"current subevent = ${runner.currentSubevent}\n"
			errorMessage ++= s"error message :"
			errorMessage ++= (exceptionLog(ex) indent 4)
			ex match
				case actionFailed: EventRuntimeException.ActionFailed =>
					errorMessage ++= "\ntg-api action: response track: "
					errorMessage ++= (GsonBuilder().setPrettyPrinting().create().toJson(
						actionFailed.response
					) indent 4) ++= "\n"
				case _ =>
			logger error errorMessage.toString
			coeur.daemons.reporter.exception(EventRuntimeException.EventListenerFailed(ex)(
				runner.currentListener, runner.currentSubevent, env
			))
		}
		
	}
	
}
