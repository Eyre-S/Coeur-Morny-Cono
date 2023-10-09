package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.{Chat, Message, Update, User}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{ForwardMessage, GetChat, SendMessage, SendSticker}

import scala.language.postfixOps

class OnCallMe (using coeur: MornyCoeur) extends EventListener {
	
	private val me = coeur.config.trustedMaster
	
	override def onMessage (using update: Update): Boolean = {
		
		if update.message.text == null then return false
		if update.message.chat.`type` != (Chat.Type Private) then return false
		
		//noinspection ScalaUnnecessaryParentheses
		val success = if me == -1 then false else
			(update.message.text toLowerCase) match
				case "steam" | "sbeam" | "sdeam" =>
					requestItem(update.message.from, "<b>STEAM LIBRARY</b>")
				case "hana paresu" | "花宫" | "内群" =>
					requestItem(update.message.from, "<b>Hana Paresu</b>")
				case "dinner" | "lunch" | "breakfast" | "meal" | "eating" | "安妮今天吃什么" =>
					requestLastDinner(update.message)
				case cc if cc startsWith "cc::" =>
					requestCustom(update.message)
				case _ =>
					return false
		
		if success then
			coeur.account exec SendSticker(
				update.message.chat.id,
				TelegramStickers ID_SENT
			).replyToMessageId(update.message.messageId)
		else
			coeur.account exec SendSticker(
				update.message.chat.id,
				TelegramStickers ID_501
			).replyToMessageId(update.message.messageId)
		
		true
		
	}
	
	private def requestItem (user: User, itemHTML: String, extra: String|Null = null): Boolean =
		coeur.account exec SendMessage(
			me,
			s"""request $itemHTML
			   |from ${user.fullnameRefHTML}${if extra == null then "" else "\n"+extra}"""
			.stripMargin
		).parseMode(ParseMode HTML)
		true
	
	private def requestLastDinner (req: Message): Boolean = {
		if coeur.config.dinnerChatId == -1 then return false
		var isAllowed = false
		var lastDinnerData: Message|Null = null
		if (coeur.trusted isTrusted_dinnerReader req.from.id) {
			// todo: have issues
			//  i dont want to test it anymore... it might be deprecated soon
			lastDinnerData = (coeur.account exec GetChat(coeur.config.dinnerChatId)).chat.pinnedMessage
			val sendResp = coeur.account exec ForwardMessage(
				req.from.id,
				lastDinnerData.forwardFromChat.id,
				lastDinnerData.forwardFromMessageId
			)
			import cc.sukazyo.cono.morny.util.CommonFormat.{formatDate, formatDuration}
			import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
			def lastDinner_dateMillis: Long = lastDinnerData.forwardDate longValue;
			coeur.account exec SendMessage(
				req.from.id,
				"<i>on</i> <code>%s [UTC+8]</code>\n- <code>%s</code> <i>before</i>".formatted(
					h(formatDate(lastDinner_dateMillis, 8)),
					h(formatDuration(lastDinner_dateMillis))
				)
			).parseMode(ParseMode HTML).replyToMessageId(sendResp.message.messageId)
			isAllowed = true
		} else {
			coeur.account exec SendSticker(
				req.from.id,
				TelegramStickers ID_403
			).replyToMessageId(req.messageId)
		}
		import Math.abs
		requestItem(
			req.from, "<b>Last Annie Dinner</b>",
			if isAllowed then s"Allowed and returned https://t.me/c/${abs(lastDinnerData.forwardFromChat.id+1000000000000L)}/${lastDinnerData.forwardFromMessageId}"
			else "Forbidden by perm check."
		)
	}
	
	private def requestCustom (message: Message): Boolean =
		requestItem(message.from, "<u>[???]</u>")
		coeur.account exec ForwardMessage(me, message.chat.id, message.messageId)
		true
	
}
