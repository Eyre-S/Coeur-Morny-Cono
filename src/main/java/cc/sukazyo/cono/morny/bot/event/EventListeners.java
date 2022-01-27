package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.bot.api.EventListenerManager;

public class EventListeners {
	
	public static final OnTelegramCommand COMMANDS_LISTENER = new OnTelegramCommand();
	public static final OnActivityRecord ACTIVITY_RECORDER = new OnActivityRecord();
	public static final OnUserSlashAction USER_SLASH_ACTION = new OnUserSlashAction();
	public static final OnUpdateTimestampOffsetLock UPDATE_TIMESTAMP_OFFSET_LOCK = new OnUpdateTimestampOffsetLock();
	public static final OnInlineQuery INLINE_QUERY = new OnInlineQuery();
	public static final OnCallMe CALL_ME = new OnCallMe();
	public static final OnEventHackHandle EVENT_HACK_HANDLE = new OnEventHackHandle();
	
	public static void registerAllListeners () {
		EventListenerManager.addListener(
				ACTIVITY_RECORDER,
				UPDATE_TIMESTAMP_OFFSET_LOCK,
				COMMANDS_LISTENER,
				USER_SLASH_ACTION,
				INLINE_QUERY,
				CALL_ME,
				EVENT_HACK_HANDLE
		);
	}
	
}
