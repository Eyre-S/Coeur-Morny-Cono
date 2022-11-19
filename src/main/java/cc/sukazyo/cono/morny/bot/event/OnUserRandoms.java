package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OnUserRandoms extends EventListener {
	
	private static final Pattern USER_OR_QUERY = Pattern.compile("(.+)(?:还是|or)(.+)");
	private static final Pattern USER_IF_QUERY = Pattern.compile("(.+)[吗?|？]+$");
	
	@Override
	public boolean onMessage (@NotNull Update update) {
		
		if (update.message().text() == null) return false;
		if (!update.message().text().startsWith("/")) return false;
		
		final String query = update.message().text().substring(1);
		String result = null;
		Matcher matcher;
		if ((matcher = USER_OR_QUERY.matcher(query)).find()) {
			result = ThreadLocalRandom.current().nextBoolean() ? matcher.group(1) : matcher.group(2);
		} else if ((matcher = USER_IF_QUERY.matcher(query)).matches()) {
			result = (ThreadLocalRandom.current().nextBoolean()?"":"不") + matcher.group(1);
		}
		
		if (result == null) return false;
		MornyCoeur.extra().exec(new SendMessage(
				update.message().chat().id(), result
		).replyToMessageId(update.message().messageId()));
		return true;
		
	}
	
}
