package cc.sukazyo.cono.morny.bot.query;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResult;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MornyQueries {
	
	private final List<ITelegramQuery<?>> queryInstances = new ArrayList<>();
	
	public MornyQueries () {
		queryInstances.add(new RawText());
	}
	
	@Nonnull
	public List<InlineQueryResult<?>> query (@Nonnull Update event) {
		final List<InlineQueryResult<?>> results = new ArrayList<>();
		for (ITelegramQuery<?> instance : queryInstances) {
			final InlineQueryResult<?> r = instance.query(event);
			if (r!=null) results.add(r);
		}
		return results;
	}
	
}
