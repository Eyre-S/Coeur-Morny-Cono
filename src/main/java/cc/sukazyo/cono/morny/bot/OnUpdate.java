package cc.sukazyo.cono.morny.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public class OnUpdate {
	
	public static int onNormalUpdate (List<Update> updates) {
		for (Update update : updates) {
			if (update.message() != null) {
				if (update.message().text() != null) {
					new Thread(() -> OnCommandExecute.searchForCommands(update)).start();
				}
			}
		}
		return UpdatesListener.CONFIRMED_UPDATES_ALL;
	}
	
}
