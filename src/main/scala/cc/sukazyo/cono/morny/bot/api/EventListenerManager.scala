package cc.sukazyo.cono.morny.bot.api

import cc.sukazyo.cono.morny.{Log, MornyCoeur}
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable
import scala.language.postfixOps

class EventListenerManager (using coeur: MornyCoeur) {
	
	private val listeners = mutable.Queue.empty[EventListener]
	
	def register (listeners: EventListener*): Unit =
		this.listeners ++= listeners
	
	private class EventRunner (using event: Update) extends Thread {
		this setName s"evt-${event.updateId()}-nn"
		private def updateThreadName (t: String): Unit =
			this setName s"evt-${event.updateId()}-$t"
		
		override def run (): Unit = {
			for (i <- listeners) {
				object status:
					var _status = 0
					def isOk: Boolean = _status > 0
					def check (u: Boolean): Unit = if u then _status = _status + 1
				try {
					updateThreadName("message")
					if event.message ne null then status check i.onMessage
					updateThreadName("edited-message")
					if event.editedMessage ne null then status check i.onEditedMessage
					updateThreadName("channel-post")
					if event.channelPost ne null then status check i.onChannelPost
					updateThreadName("edited-channel-post")
					if event.editedChannelPost ne null then status check i.onEditedChannelPost
					updateThreadName("inline-query")
					if event.inlineQuery ne null then status check i.onInlineQuery
					updateThreadName("chosen-inline-result")
					if event.chosenInlineResult ne null then status check i.onChosenInlineResult
					updateThreadName("callback-query")
					if event.callbackQuery ne null then status check i.onCallbackQuery
					updateThreadName("shipping-query")
					if event.shippingQuery ne null then status check i.onShippingQuery
					updateThreadName("pre-checkout-query")
					if event.preCheckoutQuery ne null then status check i.onPreCheckoutQuery
					updateThreadName("poll")
					if event.poll ne null then status check i.onPoll
					updateThreadName("poll-answer")
					if event.pollAnswer ne null then status check i.onPollAnswer
					updateThreadName("my-chat-member")
					if event.myChatMember ne null then status check i.onMyChatMemberUpdated
					updateThreadName("chat-member")
					if event.chatMember ne null then status check i.onChatMemberUpdated
					updateThreadName("chat-join-request")
					if event.chatJoinRequest ne null then status check i.onChatJoinRequest
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
					coeur.daemons.reporter.exception(e, "on event running")
				}
				if (status isOk) return
			}
		}
		
	}
	
	def publishUpdate (using Update): Unit = {
		EventRunner().start()
	}
	
}
