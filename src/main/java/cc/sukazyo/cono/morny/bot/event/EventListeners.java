package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.bot.api.EventListenerManager;

public class EventListeners {
	
	public static final OnCommandExecute COMMANDS_LISTENER = new OnCommandExecute();
	
	public static void registerAllListeners () {
		EventListenerManager.addListener(
				COMMANDS_LISTENER
		);
	}
	
}
