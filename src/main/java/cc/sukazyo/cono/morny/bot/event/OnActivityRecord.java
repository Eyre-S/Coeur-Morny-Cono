package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.data.tracker.TrackerDataManager;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;

public class OnActivityRecord extends EventListener {
	
	@Override
	public boolean onMessage (@Nonnull Update update) {
		if (
				update.message().chat().type() == Chat.Type.supergroup ||
				update.message().chat().type() == Chat.Type.group
		) {
			TrackerDataManager.record(
					update.message().chat().id(),
					update.message().from().id(),
					(long)update.message().date() * 1000
			);
		}
		return super.onMessage(update);
	}
	
}
