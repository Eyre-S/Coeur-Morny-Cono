package cc.sukazyo.cono.morny.bot.query;

import javax.annotation.Nullable;

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public interface ITelegramQuery {
	
	@Nullable
	List<InlineQueryUnit<?>> query (Update event);
	
}
