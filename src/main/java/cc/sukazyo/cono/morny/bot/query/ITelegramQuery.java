package cc.sukazyo.cono.morny.bot.query;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResult;

public interface ITelegramQuery <T extends InlineQueryResult<T>> {
	
	T query (Update event);
	
}
