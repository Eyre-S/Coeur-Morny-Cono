package cc.sukazyo.cono.morny.bot.query;

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;
import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MornyQueries {
	
	private final List<ITelegramQuery<?>> queryInstances = new ArrayList<>();
	
	public MornyQueries () {
		queryInstances.add(new RawText());
		queryInstances.add(new MyInformation());
	}
	
	@Nonnull
	public List<InlineQueryUnit<?>> query (@Nonnull Update event) {
		final List<InlineQueryUnit<?>> results = new ArrayList<>();
		for (ITelegramQuery<?> instance : queryInstances) {
			final InlineQueryUnit<?> r = instance.query(event);
			if (r!=null) results.add(r);
		}
		return results;
	}
	
}
