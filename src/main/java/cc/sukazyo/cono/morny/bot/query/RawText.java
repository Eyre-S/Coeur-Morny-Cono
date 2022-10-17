package cc.sukazyo.cono.morny.bot.query;

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;

import javax.annotation.Nullable;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;

import java.util.Collections;
import java.util.List;

import static cc.sukazyo.cono.morny.util.tgapi.formatting.NamedUtils.inlineIds;

public class RawText implements ITelegramQuery {
	
	public static final String ID_PREFIX = "[morny/r/text]";
	public static final String TITLE = "Raw Text";
	
	@Override
	@Nullable
	public List<InlineQueryUnit<?>> query (Update event) {
		if (event.inlineQuery().query() == null || "".equals(event.inlineQuery().query())) return null;
		return Collections.singletonList(new InlineQueryUnit<>(new InlineQueryResultArticle(
				inlineIds(ID_PREFIX, event.inlineQuery().query()), TITLE,
				new InputTextMessageContent(event.inlineQuery().query())
		)));
	}
	
}
