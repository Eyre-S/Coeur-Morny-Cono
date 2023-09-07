package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.formatting.TGToString
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.model.{Chat, Message, Update, User}
import com.pengrad.telegrambot.request.{ForwardMessage, GetChat, SendMessage, SendSticker}

import scala.language.postfixOps

object OnCallMe extends EventListener {
	
	private val me = MornyCoeur.config.trustedMaster
	
	override def onMessage (using update: Update): Boolean = {
		
		if update.message.text == null then return false
		if update.message.chat.`type` != (Chat.Type Private) then return false
		
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
		
		MornyCoeur.extra exec SendSticker(
			update.message.chat.id,
			TelegramStickers ID_SENT
		).replyToMessageId(update.message.messageId)
		true
		
	}
	
	private def requestItem (user: User, itemHTML: String, extra: String|Null = null): Unit =
		MornyCoeur.extra exec SendMessage(
			me,
			s"""request $itemHTML
			   |from ${(TGToString as user) fullnameRefHtml}${if extra == null then "" else "\n"+extra}"""
			.stripMargin
		).parseMode(ParseMode HTML)
	
	private def requestLastDinner (req: Message): Unit = {
		var isAllowed = false
		var lastDinnerData: Message|Null = null
		if (MornyCoeur.trustedInstance isTrustedForDinnerRead req.from.id) {
			lastDinnerData = (MornyCoeur.extra exec GetChat(MornyCoeur.config.dinnerChatId)).chat.pinnedMessage
			val sendResp = MornyCoeur.extra exec ForwardMessage(
				req.from.id,
				lastDinnerData.forwardFromChat.id,
				lastDinnerData.forwardFromMessageId
			)
			import cc.sukazyo.cono.morny.util.CommonFormat.{formatDate, formatDuration}
			import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml as h
			def lastDinner_dateMillis: Long = lastDinnerData.forwardDate longValue;
			MornyCoeur.extra exec SendMessage(
				req.from.id,
				"<i>on</i> <code>%s [UTC+8]</code>\n- <code>%s</code> <i>before</i>".formatted(
					h(formatDate(lastDinner_dateMillis, 8)),
					h(formatDuration(lastDinner_dateMillis))
				)
			).parseMode(ParseMode HTML).replyToMessageId(sendResp.message.messageId)
			isAllowed = true
		} else {
			MornyCoeur.extra exec SendSticker(
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
	
	private def requestCustom (message: Message): Unit =
		requestItem(message.from, "<u>[???]</u>")
		MornyCoeur.extra exec ForwardMessage(me, message.chat.id, message.messageId)
	
}
