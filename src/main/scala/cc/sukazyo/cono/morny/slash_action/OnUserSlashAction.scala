package cc.sukazyo.cono.morny.slash_action

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
import cc.sukazyo.cono.morny.util.UniversalCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps

class OnUserSlashAction (using coeur: MornyCoeur) extends EventListener {
	
	private val TG_FORMAT = "^\\w+(@\\w+)?$"r
	
	override def onMessage (using event: EventEnv): Unit = {
		
		import event.update
		val text = update.message.text
		if text == null then return;
		
		if (text startsWith "/") {
			
			// there has to be some special conditions for DP7
			// due to I have left DP7, I closed those special
			// conditions.
			// that is 2022, May 28th
			// when one year goes, These code have rewrite with
			// scala, those commented code is removed permanently.
			// these message, here to remember the old DP7.
			
			val actions = UniversalCommand.Lossy(text)
			actions(0) = actions(0) substring 1
			
			actions(0)
			
			actions(0) match
				// ignore Telegram command like
				case TG_FORMAT(_) =>
					return;
				// ignore Path link
				case x if x contains "/" => return;
				case _ =>
			
			val isHardParse = actions(0) isBlank
			def hp_len(i: Int) = if isHardParse then i+1 else i
			if isHardParse && actions.length < 2 then return
			val v_verb = actions(hp_len(0))
			val hasObject = actions.length != hp_len(1)
			val v_object =
				if hasObject then
					actions slice(hp_len(1), actions.length) mkString " "
				else ""
			val origin = update.message
			val target =
				if update.message.replyToMessage == null then
					origin
				else update.message.replyToMessage
			
			coeur.account exec SendMessage(
				update.message.chat.id,
				"%s %s%s %s %s!".format(
					origin.sender_firstnameRefHTML,
					h(v_verb), if hasObject then "" else "了",
					if (origin == target)
						s"<a href='tg://user?id=${origin.sender_id}'>自己</a>"
					else target.sender_firstnameRefHTML,
					if hasObject then h(v_object+" ") else ""
				)
			).parseMode(ParseMode HTML).replyToMessageId(update.message.messageId)
			event.setEventOk
			
		}
		
	}
	
}
