package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;
import java.util.Set;

import static cc.sukazyo.cono.morny.util.CommonRandom.probabilityTrue;

public class OnQuestionMarkReply extends EventListener {
	
	public static final Set<String> QUESTION_MARKS = Set.of("?", "？", "¿", "⁈", "⁇", "‽", "❔", "❓");
	
	@Override
	public boolean onMessage (@Nonnull Update update) {
		
		if (update.message().text() == null) return false;
		
		if (QUESTION_MARKS.contains(update.message().text()) && probabilityTrue(8)) {
			MornyCoeur.extra().exec(new SendMessage(
					update.message().chat().id(), update.message().text()
			).replyToMessageId(update.message().messageId()));
			return true;
		}
		
		return false;
		
	}
	
}
