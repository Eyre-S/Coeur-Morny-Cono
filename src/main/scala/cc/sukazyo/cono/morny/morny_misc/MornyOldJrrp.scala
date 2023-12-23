package cc.sukazyo.cono.morny.morny_misc

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.command.{ICommandAlias, ITelegramCommand}
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

class MornyOldJrrp (using coeur: MornyCoeur) extends ITelegramCommand {
	
	override val name: String = "jrrp"
	override val aliases: List[ICommandAlias] = Nil
	override val paramRule: String = ""
	override val description: String = "获取 (假的) jrrp"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		val user = event.message.from
		val jrrp = MornyJrrp.jrrp_of_telegramUser(user, System.currentTimeMillis)
		val ending = jrrp match
			case s if s > 70 => "!"
			case a if a > 30 => ";"
			case _ => "..."
		
		import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
		coeur.account exec SendMessage(
			event.message.chat.id,
			// language=html
			f"${user.fullnameRefHTML} 在(utc的)今天的运气指数是———— <code>$jrrp%.2f%%</code> ${h(ending)}"
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
		
	}
	
}
