package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;
import org.jetbrains.annotations.NotNull;

public class OnUpdateTimestampOffsetLock extends EventListener {
	
	@Override
	public boolean onMessage (@NotNull Update update) {
		return update.message().date() < MornyCoeur.latestEventTimestamp*1000;
	}
	
}
