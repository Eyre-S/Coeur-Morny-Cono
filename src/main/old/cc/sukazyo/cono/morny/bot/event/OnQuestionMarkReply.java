package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;
import java.util.Set;

import static cc.sukazyo.cono.morny.util.CommonRandom.probabilityTrue;

public class OnQuestionMarkReply extends EventListener {
	
	/**
	 * 一个 unicode 的问号字符列表. 不仅有半角全角问号，也包含了变体问号，和叹号结合的问好以及 uni-emoji 问号。
	 * @since 1.0.0-RC3.2
	 */
	public static final Set<Character> QUESTION_MARKS = Set.of('?', '？', '¿', '⁈', '⁇', '‽', '❔', '❓');
	
	@Override
	public boolean onMessage (@Nonnull Update update) {
		
		if (update.message().text() == null) return false;
		
		if (!probabilityTrue(8)) return false;
		for (char c : update.message().text().toCharArray()) {
			if (!QUESTION_MARKS.contains(c)) return false;
		}
		
		MornyCoeur.extra().exec(new SendMessage(
				update.message().chat().id(), update.message().text()
		).replyToMessageId(update.message().messageId()));
		return true;
		
	}
	
}
