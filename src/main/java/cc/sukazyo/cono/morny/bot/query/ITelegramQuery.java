package cc.sukazyo.cono.morny.bot.query;

import javax.annotation.Nullable;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResult;

public interface ITelegramQuery <T extends InlineQueryResult<T>> {
	
	@Nullable
	T query (Update event);
	
}