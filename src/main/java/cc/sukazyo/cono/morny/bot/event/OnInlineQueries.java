package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResult;
import com.pengrad.telegrambot.request.AnswerInlineQuery;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * telegram inlineQuery 功能的处理类，
 * 也是一个 InlineQueryManager（还没做）
 *
 * @since 0.4.1.3
 */
public class OnInlineQueries extends EventListener {
	
	/**
	 * @since 0.4.1.3
	 */
	@Override
	public boolean onInlineQuery (@Nonnull Update update) {
		
		List<InlineQueryUnit<?>> results = MornyCoeur.queryManager().query(update);
		
		int cacheTime = Integer.MAX_VALUE;
		boolean isPersonal = InlineQueryUnit.DEFAULT_INLINE_PERSONAL_RESP;
		InlineQueryResult<?>[] inlineQueryResults = new InlineQueryResult<?>[results.size()];
		for (int i = 0; i < results.size(); i++) {
			inlineQueryResults[i] = results.get(i).result;
			if (cacheTime > results.get(i).cacheTime()) cacheTime = results.get(i).cacheTime();
			if (results.get(i).isPersonal()) isPersonal = true;
		}
		
		if (results.size() == 0) return false;
		
		MornyCoeur.extra().exec(new AnswerInlineQuery(
				update.inlineQuery().id(), inlineQueryResults
		).cacheTime(cacheTime).isPersonal(isPersonal));
		return true;
		
	}
	
}
