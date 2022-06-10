package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.bot.api.EventListenerManager;

public class EventListeners {
	
	public static final OnTelegramCommand COMMANDS_LISTENER = new OnTelegramCommand();
	public static final OnActivityRecord ACTIVITY_RECORDER = new OnActivityRecord();
	public static final OnUserSlashAction USER_SLASH_ACTION = new OnUserSlashAction();
	public static final OnUpdateTimestampOffsetLock UPDATE_TIMESTAMP_OFFSET_LOCK = new OnUpdateTimestampOffsetLock();
	public static final OnInlineQueries INLINE_QUERY = new OnInlineQueries();
	public static final OnCallMe CALL_ME = new OnCallMe();
	public static final OnEventHackHandle EVENT_HACK_HANDLE = new OnEventHackHandle();
	@SuppressWarnings("unused") static final OnKuohuanhuanNeedSleep KUOHUANHUAN_NEED_SLEEP = new OnKuohuanhuanNeedSleep();
	public static final OnUserRandoms USER_RANDOMS = new OnUserRandoms();
	public static final OnCallMsgSend CALL_MSG_SEND = new OnCallMsgSend();
	public static final OnMedicationNotifyApply MEDICATION_NOTIFY_APPLY = new OnMedicationNotifyApply();
	
	public static void registerAllListeners () {
		EventListenerManager.addListener(
				ACTIVITY_RECORDER,
				UPDATE_TIMESTAMP_OFFSET_LOCK,
//				KUOHUANHUAN_NEED_SLEEP,
				COMMANDS_LISTENER,
				USER_RANDOMS,
				USER_SLASH_ACTION,
				INLINE_QUERY,
				CALL_ME,
				CALL_MSG_SEND,
				MEDICATION_NOTIFY_APPLY,
				EVENT_HACK_HANDLE
		);
	}
	
}
