package cc.sukazyo.cono.morny.ip186

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ITelegramCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import com.pengrad.telegrambot.model.Update

import scala.language.postfixOps

class BotCommand (using coeur: MornyCoeur) {
	import coeur.dsl.given
	
	private enum Subs (val cmd: String):
		case IP extends Subs("ip")
		case WHOIS extends Subs("whois")
	
	object IP extends ITelegramCommand:
		override val name: String = "ip"
		override val aliases: List[ICommandAlias] = Nil
		override val paramRule: String = "[ip]"
		override val description: String = "通过 https://ip.186526.xyz 查询 ip 资料"
		override def execute (using command: InputCommand, event: Update): Unit = query
	object Whois extends ITelegramCommand:
		override val name: String = "whois"
		override val aliases: List[ICommandAlias] = Nil
		override val paramRule: String = "[domain]"
		override val description: String = "通过 https://ip.186526.xyz 查询域名资料"
		override def execute (using command: InputCommand, event: Update): Unit = query
	
	private def query (using event: Update, command: InputCommand): Unit = {
		val ccMsg = Messages.derive(event.message)
		
		val target: String|Null =
			if (command.args isEmpty)
				if event.message.replyToMessage eq null then null else event.message.replyToMessage.text
			else if (command.args.length > 1)
				ccMsg("[Unavailable] Too much arguments.").send
				return
			else command.args(0)
		
		if (target eq null)
			ccMsg("[Unavailable] No ip defined.").send
			return;
		
		
		import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramParseEscape.escapeHtml as h
		try {
			
			val response = command.command match
				case Subs.IP.cmd => IP186QueryHandler.query_ip(target)
				case Subs.WHOIS.cmd => IP186QueryHandler.query_whoisPretty(target)
				case _ => throw IllegalArgumentException(s"Unknown 186-IP query method ${command.command}")
			
			ccMsg(Texts.html(
				s"""${h(response.url)}
				   |<code>${h(response.body)}</code>"""
				.stripMargin
			)).send
			
		} catch case e: Exception =>
			ccMsg(Texts.html(
				s"""[Exception] in query:
				   |<code>${h(e.getMessage)}</code>"""
				.stripMargin
			)).send
		
	}
	
}
