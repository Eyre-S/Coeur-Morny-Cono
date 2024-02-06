package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.{Log, MornyCoeur}
import cc.sukazyo.cono.morny.core.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.reporter.MornyReport
import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.UpdatesListener

import scala.collection.mutable
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
	
	private class EventRunner (using update: Update) extends Thread {
		this setName s"upd-${update.updateId()}-nn"
		private def updateThreadName (t: String): Unit =
			this setName s"upd-${update.updateId()}-$t"
		
		override def run (): Unit = {
			
			given env: EventEnv = EventEnv(update)
			
			for (i <- listeners)
				if (i.executeFilter)
					runEventListener(i)
			for (i <- listeners)
				runEventPost(i)
			
		}
		
		private def runEventPost (i: EventListener)(using EventEnv): Unit = {
			updateThreadName("#post")
			i.atEventPost
		}
		
		private def runEventListener (i: EventListener)(using EventEnv): Unit = {
			try {
				i.on
				updateThreadName("message")
				if update.message ne null then i.onMessage
				updateThreadName("edited-message")
				if update.editedMessage ne null then i.onEditedMessage
				updateThreadName("channel-post")
				if update.channelPost ne null then i.onChannelPost
				updateThreadName("edited-channel-post")
				if update.editedChannelPost ne null then i.onEditedChannelPost
				updateThreadName("inline-query")
				if update.inlineQuery ne null then i.onInlineQuery
				updateThreadName("chosen-inline-result")
				if update.chosenInlineResult ne null then i.onChosenInlineResult
				updateThreadName("callback-query")
				if update.callbackQuery ne null then i.onCallbackQuery
				updateThreadName("shipping-query")
				if update.shippingQuery ne null then i.onShippingQuery
				updateThreadName("pre-checkout-query")
				if update.preCheckoutQuery ne null then i.onPreCheckoutQuery
				updateThreadName("poll")
				if update.poll ne null then i.onPoll
				updateThreadName("poll-answer")
				if update.pollAnswer ne null then i.onPollAnswer
				updateThreadName("my-chat-member")
				if update.myChatMember ne null then i.onMyChatMemberUpdated
				updateThreadName("chat-member")
				if update.chatMember ne null then i.onChatMemberUpdated
				updateThreadName("chat-join-request")
				if update.chatJoinRequest ne null then i.onChatJoinRequest
			} catch case e => {
				val errorMessage = StringBuilder()
				errorMessage ++= "Event throws unexpected exception:\n"
				errorMessage ++= (exceptionLog(e) indent 4)
				e match
					case actionFailed: EventRuntimeException.ActionFailed =>
						errorMessage ++= "\ntg-api action: response track: "
						errorMessage ++= (GsonBuilder().setPrettyPrinting().create().toJson(
							actionFailed.response
						) indent 4) ++= "\n"
					case _ =>
				logger error errorMessage.toString
				coeur.externalContext.consume[MornyReport](_.exception(e, "on event running"))
			}
		}
		
	}
	
	
	import java.util
	import scala.jdk.CollectionConverters.*
	/** Delivery the telegram [[Update]]s.
	  *
	  * The implementation of [[UpdatesListener]].
	  *
	  * For each [[Update]], create an [[EventRunner]] for it, and
	  * start the it.
	  *
	  * @return [[UpdatesListener.CONFIRMED_UPDATES_ALL]], for all Updates
	  *         should be processed in [[EventRunner]] created for it.
	  */
	override def process (updates: util.List[Update]): Int = {
		for (update <- updates.asScala)
			EventRunner(using update).start()
		UpdatesListener.CONFIRMED_UPDATES_ALL
	}
	
}
