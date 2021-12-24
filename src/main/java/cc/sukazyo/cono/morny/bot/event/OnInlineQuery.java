package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.util.EncryptUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle;
import com.pengrad.telegrambot.model.request.InputTextMessageContent;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.AnswerInlineQuery;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.1.3
 */
public class OnInlineQuery extends EventListener {
	
	/**
	 * @since 0.4.1.3
	 */
	@Override
	public boolean onInlineQuery (@NotNull Update update) {
		MornyCoeur.getAccount().execute(new AnswerInlineQuery(update.inlineQuery().id(), new InlineQueryResultArticle[]{
				new InlineQueryResultArticle(
						EncryptUtils.encryptByMD5(update.inlineQuery().query()),
						"Raw Input",
						new InputTextMessageContent(update.inlineQuery().query()).parseMode(ParseMode.MarkdownV2)
				)
		}));
		return true;
	}
	
}
