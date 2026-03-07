package cc.sukazyo.cono.morny.call_me

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import com.pengrad.telegrambot.model.{Chat, Message, User}
import com.pengrad.telegrambot.request.{ForwardMessage, GetChat}

class OnCallMe (using coeur: MornyCoeur) extends EventListener {
	import coeur.dsl.given
	
	private val me = coeur.config.trustedMaster
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update
		val ccMsg = Messages.derive(event.update.message)
		
		if update.message.text == null then return;
		if update.message.chat.`type` != Chat.Type.Private then return
		
		//noinspection ScalaUnnecessaryParentheses
		val success: Boolean = if me == -1 then false else
			(update.message.text toLowerCase) match
				case "steam" | "sbeam" | "sdeam" =>
					requestItem(update.message.from, "<b>STEAM LIBRARY</b>")()
				case "hana paresu" | "花宫" | "内群" =>
					requestItem(update.message.from, "<b>Hana Paresu</b>")()
				case "dinner" | "lunch" | "breakfast" | "meal" | "eating" | "安妮今天吃什么" =>
					requestLastDinner(update.message)
				case cc if cc `startsWith` "cc::" =>
					requestCustom(update.message)
				case _ =>
					return;
		
		if success then {
			ccMsg.sticker(TelegramStickers.ID_SENT).send
		} else {
			ccMsg.sticker(TelegramStickers.ID_501).send
		}
		
		event.setEventOk
		
	}
	
	private def requestItem (user: User, itemHTML: String)(extra: String|Null = null): Boolean =
		Messages.create(me)(Texts.html(
			s"""request $itemHTML
			   |from ${user.fullnameRefHTML}${if extra == null then "" else "\n"+extra}"""
				.stripMargin
		)).send
		true
	
	private def requestLastDinner (req: Message): Boolean = {
		
		import Math.abs
		if coeur.config.dinnerChatId == -1 then return false
		val resp = requestItem(req.from, "<b>Last Annie Dinner</b>")
		
		if (coeur.trusted isTrust4dinner req.from) {
			// todo: have issues
			//  i dont want to test it anymore... it might be deprecated soon
			// todo: pinnedMessage maybe null if no message pinned
			val lastDinnerData: Message = GetChat(coeur.config.dinnerChatId).unsafeExecute.chat.pinnedMessage
			val sendResp = ForwardMessage(
				req.from.id,
				lastDinnerData.chat.id,
				lastDinnerData.messageId
			).unsafeExecute
			import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramParseEscape.escapeHtml as h
			import cc.sukazyo.cono.morny.system.utils.EpochDateTime.EpochMillis
			import cc.sukazyo.cono.morny.util.CommonFormat.{formatDate, formatDuration}
			def lastDinner_dateMillis: EpochMillis = EpochMillis fromSeconds lastDinnerData.forwardOrigin.date
			Messages.create(req.from.id).replyTo(sendResp.message.messageId)(Texts.html(
				"<i>on</i> <code>%s [UTC+8]</code>\n- <code>%s</code> <i>before</i>".formatted(
					h(formatDate(lastDinner_dateMillis, 8)),
					h(formatDuration(System.currentTimeMillis - lastDinner_dateMillis))
				)
			)).send
			resp(s"Allowed and returned https://t.me/c/${abs(lastDinnerData.chat.id+1000000000000L)}/${lastDinnerData.messageId}")
		} else {
			Messages.derive(req)
				.sticker(TelegramStickers.ID_403)
				.send
			resp("Forbidden by perm check.")
		}
	}
	
	private def requestCustom (message: Message): Boolean =
		requestItem(message.from, "<u>[???]</u>")()
		ForwardMessage(me, message.chat.id, message.messageId)
			.unsafeExecute
		true
	
}
