package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

@Deprecated
public class OnKuohuanhuanNeedSleep implements EventListener {
	
	@Override
	public boolean onMessage (@Nonnull Update update) {
		final GregorianCalendar time = new GregorianCalendar(Locale.TAIWAN);
		time.setTimeInMillis(System.currentTimeMillis());
		if (
				( update.message().from().id() == 786563752L && (
						time.get(Calendar.HOUR_OF_DAY) >= 23 ||
						time.get(Calendar.HOUR_OF_DAY) < 5
				)) || ( update.message().from().id() == 1075871712L && (
						(time.get(Calendar.HOUR_OF_DAY) >= 22 && time.get(Calendar.MINUTE) >= 30) ||
						time.get(Calendar.HOUR_OF_DAY) >= 23 ||
						time.get(Calendar.HOUR_OF_DAY) < 5
				))
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
