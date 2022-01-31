package cc.sukazyo.cono.morny.bot.query;

import javax.annotation.Nullable;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.model.request.ParseMode;

import cc.sukazyo.cono.morny.util.TelegramUserInformation;

public class MyInformation implements ITelegramQuery<InlineQueryResultArticle> {
	
	public static final String ID_PREFIX = "[morny/info/me]";
	public static final String TITLE = "My Account Information";
	
	@Override
	@Nullable
	public InlineQueryResultArticle query(Update event) {
		if (!(event.inlineQuery().query() == null || "".equals(event.inlineQuery().query()))) return null;
		return new InlineQueryResultArticle(ID_PREFIX, TITLE, new InputTextMessageContent(
				TelegramUserInformation.informationOutputHTML(event.inlineQuery().from())
		).parseMode(ParseMode.HTML));
	}
	
}
