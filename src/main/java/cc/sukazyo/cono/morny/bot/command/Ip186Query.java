package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.ip186.IP186QueryResponse;
import cc.sukazyo.untitled.util.telegram.object.InputCommand;
import cc.sukazyo.cono.morny.data.ip186.IP186QueryHandler;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cc.sukazyo.untitled.util.telegram.formatting.MsgEscape.escapeHtml;

/**
 * {@value IP186QueryHandler#SITE_URL} 查询的 telegram 命令前端
 * @since 0.4.2.10
 */
public class Ip186Query {
	
	public static class Ip implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "/ip"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Nonnull @Override public String getParamRule () { return "[ip]"; }
		@Nonnull @Override public String getDescription () { return "通过 https://ip.186526.xyz 查询 ip 资料"; }
		@Override public void execute (@NotNull InputCommand command, @NotNull Update event) { exec(event, command); }
	}
	
	public static class Whois implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "/whois"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Nonnull @Override public String getParamRule () { return "[domain]"; }
		@Nonnull @Override public String getDescription () { return "通过 https://ip.186526.xyz 查询域名资料"; }
		@Override public void execute (@NotNull InputCommand command, @NotNull Update event) { exec(event, command); }
	}
	
	private static void exec (@Nonnull Update event, @Nonnull InputCommand command) {
		
		String arg = null;
		if (!command.hasArgs()) {
			if (event.message().replyToMessage() != null) {
				arg = event.message().replyToMessage().text();
			}
		} else if (command.getArgs().length > 1) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"[Unavailable] Too much arguments."
			).replyToMessageId(event.message().messageId()));
			return;
		} else {
			arg = command.getArgs()[0];
		}
		if (arg == null) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"[Unavailable] No ip defined."
			).replyToMessageId(event.message().messageId()));
			return;
		}
		
		try {
			IP186QueryResponse response = switch (command.getCommand()) {
				case "/ip" -> IP186QueryHandler.queryIp(arg);
				case "/whois" -> IP186QueryHandler.queryWhoisPretty(arg);
				default -> throw new IllegalArgumentException("Unknown 186-IP query method " + command.getCommand());
			};
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					escapeHtml(response.url()) + "\n<code>" + escapeHtml(response.body()) + "</code>"
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
		} catch (Exception e) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"[Exception] in query:\n<code>" + escapeHtml(e.getMessage()) + "</code>"
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
		}
		
	}
	
}
