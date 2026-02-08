package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.event.TelegramBotEvents
import cc.sukazyo.cono.morny.core.{Log, MornyCoeur}
import cc.sukazyo.cono.morny.system.telegram_api.action.ClientRequestException
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.{TelegramException, UpdatesListener}

import scala.collection.mutable

/** Contains a [[scala.collection.mutable.Queue]] of [[EventListener]], and delivery telegram [[Update]].
  *
  * Implemented [[process]] in [[UpdatesListener]] so it can directly used in [[com.pengrad.telegrambot.TelegramBot.setupListener]].
  *
  * @param coeur the [[MornyCoeur]] context.
  */
class EventListenerManager (using coeur: MornyCoeur) extends UpdatesListener {
	
	object ExceptionHandler extends com.pengrad.telegrambot.ExceptionHandler {
		override def onException (e: TelegramException): Unit = {
			
			// This function intended to catch exceptions on update
			//   fetching controlled by Telegram API Client. So that
			//   it won't be directly printed to STDOUT without Morny's
			//   logger. And it can be reported when needed.
			// TelegramException can either contain a caused that infers
			//   a lower level client exception (network err or others);
			//   nor contains a response that means API request failed.
			
			if (e.response != null) {
				import com.google.gson.GsonBuilder
				logger `error`
					s"""Failed get updates: ${e.getMessage}
					   |  server responses:
					   |${GsonBuilder().setPrettyPrinting().create.toJson(e.response).indent(4)}
					   |""".stripMargin
			}
			
			if (e.getCause != null) {
				import java.net.{SocketException, SocketTimeoutException}
				import javax.net.ssl.SSLHandshakeException
				val caused = e.getCause
				caused match
					case e_timeout: (SSLHandshakeException|SocketException|SocketTimeoutException) =>
						import cc.sukazyo.messiva.log.Message

						import scala.collection.mutable
						val log = mutable.ArrayBuffer(s"Failed get updates: Network Error")
						var current: Throwable = e_timeout
						log += s"  due to: ${current.getClass.getSimpleName}: ${current.getMessage}"
						while (current.getCause != null) {
							current = current.getCause
							log += s"  caused by: ${current.getClass.getSimpleName}: ${current.getMessage}"
						}
						logger `error` Message(log mkString "\n")
					case e_other =>
						logger `error`
							s"""Failed get updates:
							   |${e_other.toLogString `indent` 3}""".stripMargin
				
				TelegramBotEvents.inCoeur.OnGetUpdateFailed.emit(e)
				
			}
			
		}
	}
	
	private val listeners = mutable.Queue.empty[EventListener]
	
	infix def register (listener: EventListener): Unit =
		this.listeners += listener
	
	def register (listeners: EventListener*): Unit =
		this.listeners ++= listeners
	
	private class EventRunner (using update: Update) extends Thread {
		this `setName` s"upd-${update.updateId()}-nn"
		private def updateThreadName (t: String): Unit =
			this `setName` s"upd-${update.updateId()}-$t"
		
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
		
		private def runEventListener (i: EventListener)(using event: EventEnv): Unit = {
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
			} catch case e: Throwable => {
				val errorMessage = StringBuilder()
				errorMessage ++= "Event throws unexpected exception:\n"
				errorMessage ++= (e.toLogString `indent` 4)
				e match
					case actionFailed: ClientRequestException.ActionFailed =>
						errorMessage ++= "\ntg-api action: response track: "
						errorMessage ++= (GsonBuilder().setPrettyPrinting().create().toJson(
							actionFailed.response
						) `indent` 4) ++= "\n"
					case _ =>
				logger `error` errorMessage.toString
				TelegramBotEvents.inCoeur.OnListenerOccursException.emit((e, i, event))
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
