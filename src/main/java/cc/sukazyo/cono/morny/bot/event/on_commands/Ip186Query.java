package cc.sukazyo.cono.morny.bot.event.on_commands;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.InputCommand;
import cc.sukazyo.cono.morny.data.ip186.IP186QueryResponse;
import cc.sukazyo.cono.morny.data.ip186.IP186QueryHandler;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;

import static cc.sukazyo.cono.morny.util.StringUtils.escapeHtmlTelegram;

/**
 * {@value IP186QueryHandler#SITE_URL} 查询的 telegram 命令前端
 * @since 0.4.2.10
 */
public class Ip186Query {
	
	public static void exec (@Nonnull Update event, @Nonnull InputCommand command) {
		
		String arg = null;
		if (!command.hasArgs()) {
			if (event.message().replyToMessage() != null) {
				arg = event.message().replyToMessage().text();
			}
		} else if (command.getArgs().length > 1) {
			MornyCoeur.getAccount().execute(new SendMessage(
					event.message().chat().id(),
					"[Unavailable] Too much arguments."
			).replyToMessageId(event.message().messageId()));
			return;
		} else {
			arg = command.getArgs()[0];
		}
		if (arg == null) {
			MornyCoeur.getAccount().execute(new SendMessage(
					event.message().chat().id(),
					"[Unavailable] No ip defined."
			).replyToMessageId(event.message().messageId()));
			return;
		}
		
		try {
			IP186QueryResponse response = switch (command.getCommand()) {
				case "/ip" -> IP186QueryHandler.queryIp(arg);
				case "/whois" -> IP186QueryHandler.queryWhois(arg);
				default -> throw new IllegalArgumentException("Unknown 186-IP query method " + command.getCommand());
			};
			MornyCoeur.getAccount().execute(new SendMessage(
					event.message().chat().id(),
					escapeHtmlTelegram(response.url()) + "\n<code>" + escapeHtmlTelegram(response.body()) + "</code>"
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
		} catch (Exception e) {
			MornyCoeur.getAccount().execute(new SendMessage(
					event.message().chat().id(),
					"[Exception] in query:\n<code>" + escapeHtmlTelegram(e.getMessage()) + "</code>"
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
		}
		
	}
	
}
