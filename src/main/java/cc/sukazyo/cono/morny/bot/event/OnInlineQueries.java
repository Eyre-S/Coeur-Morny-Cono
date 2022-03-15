package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
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
		
		List<InlineQueryResult<?>> results = MornyCoeur.queryManager().query(update);
		
		if (results.size() == 0) return false;
		
		MornyCoeur.extra().exec(new AnswerInlineQuery(update.inlineQuery().id(), results.toArray(InlineQueryResult[]::new)));
		return true;
		
	}
	
}
