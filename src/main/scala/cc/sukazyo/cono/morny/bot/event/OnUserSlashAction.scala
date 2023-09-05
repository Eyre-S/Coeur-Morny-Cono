package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.util.UniversalCommand
import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml as h
import cc.sukazyo.cono.morny.util.tgapi.formatting.TGToString
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps

object OnUserSlashAction extends EventListener {
	
	private val TG_FORMAT = "^\\w+(@\\w+)?$"r
	
	override def onMessage (update: Update): Boolean = {
		
		val text = update.message.text;
		if text == null then return false
		
		if (text startsWith "/") {
			
			val actions = UniversalCommand format text
			actions(0) = actions(0) substring 1
			
			actions(0)
			
			actions(0) match
				case TG_FORMAT(_) =>
					return false
				case x if x contains "/" => return false
			
			val isHardParse = actions(0) isBlank
			def hp_len(i: Int) = if isHardParse then i+1 else i
			if isHardParse && actions.length < 2 then return false
			val v_verb = actions(hp_len(0))
			val hasObject = actions.length != hp_len(1)
			val v_object =
				if hasObject then
					actions slice(hp_len(1), actions.length) mkString(" ")
				else ""
			val origin = update.message
			val target =
				if update.message.replyToMessage == null then
					origin
				else update.message.replyToMessage
			
			MornyCoeur.extra exec SendMessage(
				update.message.chat.id,
				"%s %s%s %s %s!".format(
					(TGToString as origin) getSenderFirstNameRefHtml,
					h(v_verb), if hasObject then "" else "了",
					if (origin == target)
						s"<a href='tg://user?id=${(TGToString as target) getSenderId}'>自己</a>"
					else (TGToString as target) getSenderFirstNameRefHtml,
					if hasObject then h(v_object+" ") else ""
				)
			).parseMode(ParseMode HTML).replyToMessageId(update.message.messageId)
			true
			
		} else false
		
	}
	
}
