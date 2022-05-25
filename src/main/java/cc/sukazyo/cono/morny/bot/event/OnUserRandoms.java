package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.untitled.util.command.CommonCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnUserRandoms extends EventListener {
	
	private static final Pattern USER_OR_CN_QUERY = Pattern.compile("(.+)还是(.+)");
	private static final Pattern USER_OR_EN_QUERY = Pattern.compile("(.+)or(.+)");
	
	@Override
	public boolean onMessage (@NotNull Update update) {
		
		if (update.message().text() == null) return false;
		if (!update.message().text().startsWith("/")) return false;
		
		final String[] preProcess = CommonCommand.format(update.message().text());
		if (preProcess.length > 1) return false;
		final String query = preProcess[0];
		
		// ----- START CODE BLOCK COMMENT -----
		// 这里实现思路和代码优化有至少一半是 copilot 和 IDEA 提供的
		// 实现思路都可以从人类手里抢一半贡献太恐怖了aba
		String result = null;
		final Matcher matcher;
		if (query.contains("还是")) {
			matcher = USER_OR_CN_QUERY.matcher(query);
		} else {
			matcher = USER_OR_EN_QUERY.matcher(query);
		}
		if (matcher.find()) {
			result = ThreadLocalRandom.current().nextBoolean() ? matcher.group(1) : matcher.group(2);
		}
		// ----- STOP CODE BLOCK COMMENT -----
		
		if (result == null) return false;
		MornyCoeur.extra().exec(new SendMessage(
				update.message().chat().id(), result
		).replyToMessageId(update.message().messageId()));
		return true;
		
	}
	
}
