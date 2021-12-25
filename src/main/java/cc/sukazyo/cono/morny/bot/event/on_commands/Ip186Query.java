package cc.sukazyo.cono.morny.bot.event.on_commands;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.InputCommand;
import cc.sukazyo.cono.morny.data.ip186.IP186QueryResponse;
import cc.sukazyo.cono.morny.data.ip186.IP186QueryHandler;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nonnull;

public class Ip186Query {
	
	public static void exec (@Nonnull Update event, @Nonnull InputCommand command) {
		
		if (!command.hasArgs()) { MornyCoeur.getAccount().execute(new SendMessage(
				event.message().chat().id(),
				"[Unavailable] No ip defined."
		).replyToMessageId(event.message().messageId())); return; }
		
		if (command.getArgs().length > 1) { MornyCoeur.getAccount().execute(new SendMessage(
				event.message().chat().id(),
				"[Unavailable] Too much arguments."
		).replyToMessageId(event.message().messageId())); return; }
		
		try {
			IP186QueryResponse response = switch (command.getCommand()) {
				case "/ip" -> IP186QueryHandler.queryIp(command.getArgs()[0]);
				case "/whois" -> IP186QueryHandler.queryWhois(command.getArgs()[0]);
				default -> throw new IllegalArgumentException("Unknown 186-IP query method " + command.getCommand());
			};
			MornyCoeur.getAccount().execute(new SendMessage(
					event.message().chat().id(),
					response.url() + "\n<code>" + StringEscapeUtils.escapeHtml4(response.body()) + "</code>"
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
		} catch (Exception e) {
			MornyCoeur.getAccount().execute(new SendMessage(
					event.message().chat().id(),
					"[Exception] in query:\n<code>" + e.getMessage() + "</code>"
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
		}
		
	}
	
}
