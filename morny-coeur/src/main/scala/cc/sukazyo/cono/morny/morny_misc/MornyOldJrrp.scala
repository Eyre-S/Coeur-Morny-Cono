package cc.sukazyo.cono.morny.morny_misc

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ITelegramCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import com.pengrad.telegrambot.model.Update

class MornyOldJrrp (using coeur: MornyCoeur) extends ITelegramCommand {
	import coeur.dsl.given
	
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
		
		import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramParseEscape.escapeHtml as h
		Messages.derive(event.message)(Texts.html(
			// language=html
			f"${user.fullnameRefHTML} 在(utc的)今天的运气指数是———— <code>$jrrp%.2f%%</code> ${h(ending)}"
		)).send
		
	}
	
}
