package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cc.sukazyo.cono.morny.util.CommonRandom.iif;

public class OnUserRandoms extends EventListener {
	
	private static final Pattern USER_OR_QUERY = Pattern.compile("(.+)(?:还是|or)(.+)");
	private static final Pattern USER_IF_QUERY = Pattern.compile("(.+)[吗?|？]+$");
	
	@Override
	public boolean onMessage (@Nonnull Update update) {
		
		if (update.message().text() == null) return false;
		if (!update.message().text().startsWith("/")) return false;
		
		final String query = update.message().text().substring(1);
		String result = null;
		Matcher matcher;
		if ((matcher = USER_OR_QUERY.matcher(query)).find()) {
			result = iif() ? matcher.group(1) : matcher.group(2);
		} else if ((matcher = USER_IF_QUERY.matcher(query)).matches()) {
			result = (iif()?"":"不") + matcher.group(1);
		}
		
		if (result == null) return false;
		MornyCoeur.extra().exec(new SendMessage(
				update.message().chat().id(), result
		).replyToMessageId(update.message().messageId()));
		return true;
		
	}
	
}
