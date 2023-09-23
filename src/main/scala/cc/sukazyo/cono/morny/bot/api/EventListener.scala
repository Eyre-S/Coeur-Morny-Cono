package cc.sukazyo.cono.morny.bot.api

import cc.sukazyo.cono.morny.MornyCoeur
import com.pengrad.telegrambot.model.Update

trait EventListener (using MornyCoeur) {
	
	def onMessage (using Update): Boolean = false
	def onEditedMessage (using Update): Boolean = false
	def onChannelPost (using Update): Boolean = false
	def onEditedChannelPost (using Update): Boolean = false
	def onInlineQuery (using Update): Boolean = false
	def onChosenInlineResult (using Update): Boolean = false
	def onCallbackQuery (using Update): Boolean = false
	def onShippingQuery (using Update): Boolean = false
	def onPreCheckoutQuery (using Update): Boolean = false
	def onPoll (using Update): Boolean = false
	def onPollAnswer (using Update): Boolean = false
	def onMyChatMemberUpdated (using Update): Boolean = false
	def onChatMemberUpdated (using Update): Boolean = false
	def onChatJoinRequest (using Update): Boolean = false
	
}
