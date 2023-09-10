package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.ip186.IP186QueryHandler
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps

object IP186Query {
	
	private enum Subs (val cmd: String):
		case IP extends Subs("ip")
		case WHOIS extends Subs("whois")
	
	object IP extends ITelegramCommand:
		override val name: String = "ip"
		override val aliases: Array[ICommandAlias]|Null = null
		override val paramRule: String = "[ip]"
		override val description: String = "通过 https://ip.186526.xyz 查询 ip 资料"
		override def execute (using command: InputCommand, event: Update): Unit = query
	object Whois extends ITelegramCommand:
		override val name: String = "whois"
		override val aliases: Array[ICommandAlias]|Null = null
		override val paramRule: String = "[domain]"
		override val description: String = "通过 https://ip.186526.xyz 查询域名资料"
		override def execute (using command: InputCommand, event: Update): Unit = query
	
	private def query (using event: Update, command: InputCommand): Unit = {
		
		val target: String|Null =
			if (command.getArgs isEmpty)
				if event.message.replyToMessage eq null then null else event.message.replyToMessage.text
			else if (command.getArgs.length > 1)
				MornyCoeur.extra exec SendMessage(
					event.message.chat.id,
					"[Unavailable] Too much arguments."
				).replyToMessageId(event.message.messageId)
				return
			else command.getArgs()(0)
		
		if (target eq null)
			MornyCoeur.extra exec new SendMessage(
				event.message.chat.id,
				"[Unavailable] No ip defined."
			).replyToMessageId(event.message.messageId)
			return;
		
		try {
			
			val response = command.getCommand match
				case Subs.IP.cmd => IP186QueryHandler.query_ip(target)
				case Subs.WHOIS.cmd => IP186QueryHandler.query_whoisPretty(target)
				case _ => throw IllegalArgumentException(s"Unknown 186-IP query method ${command.getCommand}")
			
			import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml as h
			MornyCoeur.extra exec SendMessage(
				event.message.chat.id,
				s"""${h(response.url)}
				   |<code>${h(response.body)}</code>"""
				.stripMargin
			).parseMode(ParseMode HTML).replyToMessageId(event.message.messageId)
			
		} catch case e: Exception =>
			import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml as h
			MornyCoeur.extra exec new SendMessage(
				event.message().chat().id(),
				s"""[Exception] in query:
				   |<code>${h(e.getMessage)}</code>"""
				.stripMargin
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId())
		
	}
	
}
