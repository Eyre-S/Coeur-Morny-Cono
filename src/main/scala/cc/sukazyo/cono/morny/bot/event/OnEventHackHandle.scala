package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

import scala.collection.mutable
import scala.language.postfixOps

class OnEventHackHandle (using coeur: MornyCoeur) extends EventListener {
	
	import coeur.daemons.eventHack.trigger
	
	override def onMessage (using update: Update): Boolean =
		trigger(update.message.chat.id, update.message.from.id)
	override def onEditedMessage (using update: Update): Boolean =
		trigger(update.editedMessage.chat.id, update.editedMessage.from.id)
	override def onChannelPost (using update: Update): Boolean =
		trigger(update.channelPost.chat.id, 0)
	override def onEditedChannelPost (using update: Update): Boolean =
		trigger(update.editedChannelPost.chat.id, 0)
	override def onInlineQuery (using update: Update): Boolean =
		trigger(0, update.inlineQuery.from.id)
	override def onChosenInlineResult (using update: Update): Boolean =
		trigger(0, update.chosenInlineResult.from.id)
	override def onCallbackQuery (using update: Update): Boolean =
		trigger(0, update.callbackQuery.from.id)
	override def onShippingQuery (using update: Update): Boolean =
		trigger(0, update.shippingQuery.from.id)
	override def onPreCheckoutQuery (using update: Update): Boolean =
		trigger(0, update.preCheckoutQuery.from.id)
	override def onPoll (using update: Update): Boolean =
		trigger(0, 0)
	override def onPollAnswer (using update: Update): Boolean =
		trigger(0, update.pollAnswer.user.id)
	override def onMyChatMemberUpdated (using update: Update): Boolean =
		trigger(update.myChatMember.chat.id, update.myChatMember.from.id)
	override def onChatMemberUpdated (using update: Update): Boolean =
		trigger(update.chatMember.chat.id, update.chatMember.from.id)
	override def onChatJoinRequest (using update: Update): Boolean =
		trigger(update.chatJoinRequest.chat.id, update.chatJoinRequest.from.id)
	
}
