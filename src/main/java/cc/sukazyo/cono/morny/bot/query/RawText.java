package cc.sukazyo.cono.morny.bot.query;

import cc.sukazyo.cono.morny.util.EncryptUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;

public class RawText implements ITelegramQuery<InlineQueryResultArticle> {
	
	public static final String TITLE = "Raw Text";
	
	@Override
	public InlineQueryResultArticle query (Update event) {
		return new InlineQueryResultArticle(
				"[morny/r/txt]" + EncryptUtils.encryptByMD5(event.inlineQuery().query()),
				TITLE,
				new InputTextMessageContent(event.inlineQuery().query())
		);
	}
	
}
