package cc.sukazyo.cono.morny.bot.query;

import javax.annotation.Nullable;

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResult;

public interface ITelegramQuery <T extends InlineQueryResult<T>> {
	
	@Nullable
	InlineQueryUnit<T> query (Update event);
	
}
