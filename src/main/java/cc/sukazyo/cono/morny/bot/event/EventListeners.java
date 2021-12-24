package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.bot.api.EventListenerManager;

public class EventListeners {
	
	public static final OnCommandExecute COMMANDS_LISTENER = new OnCommandExecute();
	public static final OnActivityRecord ACTIVITY_RECORDER = new OnActivityRecord();
	public static final OnUserSlashAction USER_SLASH_ACTION = new OnUserSlashAction();
	public static final OnUpdateTimestampOffsetLock UPDATE_TIMESTAMP_OFFSET_LOCK = new OnUpdateTimestampOffsetLock();
	public static final OnInlineQuery INLINE_QUERY = new OnInlineQuery();
	
	public static void registerAllListeners () {
		EventListenerManager.addListener(
				ACTIVITY_RECORDER,
				UPDATE_TIMESTAMP_OFFSET_LOCK,
				COMMANDS_LISTENER,
				USER_SLASH_ACTION,
				INLINE_QUERY
		);
	}
	
}
