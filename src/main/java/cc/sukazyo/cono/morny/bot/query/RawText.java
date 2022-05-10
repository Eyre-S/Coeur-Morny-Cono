package cc.sukazyo.cono.morny.bot.query;

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;
import cc.sukazyo.cono.morny.util.EncryptUtils;

import javax.annotation.Nullable;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;

public class RawText implements ITelegramQuery<InlineQueryResultArticle> {
	
	public static final String ID_PREFIX = "[morny/r/text]";
	public static final String TITLE = "Raw Text";
	
	@Override
	@Nullable
	public InlineQueryUnit<InlineQueryResultArticle> query (Update event) {
		if (event.inlineQuery().query() == null || "".equals(event.inlineQuery().query())) return null;
		return new InlineQueryUnit<>(new InlineQueryResultArticle(
				ID_PREFIX + EncryptUtils.encryptByMD5(event.inlineQuery().query()),
				TITLE,
				new InputTextMessageContent(event.inlineQuery().query())
		));
	}
	
}
