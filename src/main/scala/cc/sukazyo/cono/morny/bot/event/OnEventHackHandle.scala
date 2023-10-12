package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

import scala.collection.mutable
import scala.language.postfixOps

class OnEventHackHandle (using coeur: MornyCoeur) extends EventListener {
	
	private def trigger (chat_id: Long, from_id: Long)(using event: EventEnv): Unit =
		given Update = event.update
		if coeur.daemons.eventHack.trigger(chat_id, from_id) then
			event.setEventOk
	
	override def onMessage (using event: EventEnv): Unit =
		trigger(event.update.message.chat.id, event.update.message.from.id)
	override def onEditedMessage (using event: EventEnv): Unit =
		trigger(event.update.editedMessage.chat.id, event.update.editedMessage.from.id)
	override def onChannelPost (using event: EventEnv): Unit =
		trigger(event.update.channelPost.chat.id, 0)
	override def onEditedChannelPost (using event: EventEnv): Unit =
		trigger(event.update.editedChannelPost.chat.id, 0)
	override def onInlineQuery (using event: EventEnv): Unit =
		trigger(0, event.update.inlineQuery.from.id)
	override def onChosenInlineResult (using event: EventEnv): Unit =
		trigger(0, event.update.chosenInlineResult.from.id)
	override def onCallbackQuery (using event: EventEnv): Unit =
		trigger(0, event.update.callbackQuery.from.id)
	override def onShippingQuery (using event: EventEnv): Unit =
		trigger(0, event.update.shippingQuery.from.id)
	override def onPreCheckoutQuery (using event: EventEnv): Unit =
		trigger(0, event.update.preCheckoutQuery.from.id)
	override def onPoll (using event: EventEnv): Unit =
		trigger(0, 0)
	override def onPollAnswer (using event: EventEnv): Unit =
		trigger(0, event.update.pollAnswer.user.id)
	override def onMyChatMemberUpdated (using event: EventEnv): Unit =
		trigger(event.update.myChatMember.chat.id, event.update.myChatMember.from.id)
	override def onChatMemberUpdated (using event: EventEnv): Unit =
		trigger(event.update.chatMember.chat.id, event.update.chatMember.from.id)
	override def onChatJoinRequest (using event: EventEnv): Unit =
		trigger(event.update.chatJoinRequest.chat.id, event.update.chatJoinRequest.from.id)
	
}
