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

object OnEventHackHandle extends EventListener {
	
	private case class Hacker (from_chat: Long, from_message: Long):
		override def toString: String = s"$from_chat/$from_message"
	enum HackType:
		case USER
		case GROUP
		case ANY
	
	private val hackers = mutable.HashMap.empty[String, Hacker]
	
	def registerHack (from_message: Long, from_user: Long, from_chat: Long, t: HackType): Unit =
		val record = t match
			case HackType.USER => s"(($from_user))"
			case HackType.GROUP => s"{{$from_chat}}"
			case HackType.ANY => "[[]]"
		hackers += (record -> Hacker(from_chat, from_message))
		logger debug s"add hacker track $record"
	
	private def onEventHacked (chat: Long, fromUser: Long)(using update: Update): Boolean = {
		logger debug s"got event signed {{$chat}}(($fromUser))"
		val x: Hacker =
			if hackers contains s"(($fromUser))" then (hackers remove s"(($fromUser))")get
			else if hackers contains s"{{$chat}}" then (hackers remove s"{{$chat}}")get
			else if hackers contains "[[]]" then (hackers remove "[[]]")get
			else return false
		logger debug s"hacked event by $x"
		import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
		MornyCoeur.extra exec SendMessage(
			x.from_chat,
			// language=html
			s"<code>${h(GsonBuilder().setPrettyPrinting().create.toJson(update))}</code>"
		).parseMode(ParseMode HTML).replyToMessageId(x.from_message toInt)
		true
	}
	
	override def onMessage (using update: Update): Boolean =
		onEventHacked(update.message.chat.id, update.message.from.id)
	override def onEditedMessage (using update: Update): Boolean =
		onEventHacked(update.editedMessage.chat.id, update.editedMessage.from.id)
	override def onChannelPost (using update: Update): Boolean =
		onEventHacked(update.channelPost.chat.id, 0)
	override def onEditedChannelPost (using update: Update): Boolean =
		onEventHacked(update.editedChannelPost.chat.id, 0)
	override def onInlineQuery (using update: Update): Boolean =
		onEventHacked(0, update.inlineQuery.from.id)
	override def onChosenInlineResult (using update: Update): Boolean =
		onEventHacked(0, update.chosenInlineResult.from.id)
	override def onCallbackQuery (using update: Update): Boolean =
		onEventHacked(0, update.callbackQuery.from.id)
	override def onShippingQuery (using update: Update): Boolean =
		onEventHacked(0, update.shippingQuery.from.id)
	override def onPreCheckoutQuery (using update: Update): Boolean =
		onEventHacked(0, update.preCheckoutQuery.from.id)
	override def onPoll (using update: Update): Boolean =
		onEventHacked(0, 0)
	override def onPollAnswer (using update: Update): Boolean =
		onEventHacked(0, update.pollAnswer.user.id)
	override def onMyChatMemberUpdated (using update: Update): Boolean =
		onEventHacked(update.myChatMember.chat.id, update.myChatMember.from.id)
	override def onChatMemberUpdated (using update: Update): Boolean =
		onEventHacked(update.chatMember.chat.id, update.chatMember.from.id)
	override def onChatJoinRequest (using update: Update): Boolean =
		onEventHacked(update.chatJoinRequest.chat.id, update.chatJoinRequest.from.id)
	
}
