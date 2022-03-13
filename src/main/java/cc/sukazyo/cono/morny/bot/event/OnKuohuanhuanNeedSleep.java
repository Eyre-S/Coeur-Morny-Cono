package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class OnKuohuanhuanNeedSleep extends EventListener {
	
	@Override
	public boolean onMessage (@Nonnull Update update) {
		if (
				update.message().from().id() == 786563752L && (
						new GregorianCalendar(Locale.TAIWAN).get(Calendar.HOUR_OF_DAY) >= 23 ||
						new GregorianCalendar(Locale.TAIWAN).get(Calendar.HOUR_OF_DAY) < 5
				)
		) {
			MornyCoeur.extra().exec(
					new DeleteMessage(update.message().chat().id(),
							update.message().messageId())
			);
			return true;
		}
		return false;
	}
	
}
