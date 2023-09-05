package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.bot.api.EventListenerManager;
import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit;

public class EventListeners {
	
	public static final OnTelegramCommand COMMANDS_LISTENER = new OnTelegramCommand();
//	public static final OnActivityRecord ACTIVITY_RECORDER = new OnActivityRecord();
	public static final OnUpdateTimestampOffsetLock UPDATE_TIMESTAMP_OFFSET_LOCK = new OnUpdateTimestampOffsetLock();
	public static final OnEventHackHandle EVENT_HACK_HANDLE = new OnEventHackHandle();
//	static final OnKuohuanhuanNeedSleep KUOHUANHUAN_NEED_SLEEP = new OnKuohuanhuanNeedSleep();
	public static final OnCallMsgSend CALL_MSG_SEND = new OnCallMsgSend();
	public static final OnMedicationNotifyApply MEDICATION_NOTIFY_APPLY = new OnMedicationNotifyApply();
	public static final OnRandomlyTriggered RANDOMLY_TRIGGERED = new OnRandomlyTriggered();
	public static final OnUniMeowTrigger UNI_MEOW_TRIGGER = new OnUniMeowTrigger();
	public static final OnQuestionMarkReply QUESTION_MARK_REPLY = new OnQuestionMarkReply();
	
	public static void registerAllListeners () {
		EventListenerManager.addListener(
//				ACTIVITY_RECORDER,
				UPDATE_TIMESTAMP_OFFSET_LOCK,
				/* write functional event behind here */
//				KUOHUANHUAN_NEED_SLEEP,
				COMMANDS_LISTENER,
				UNI_MEOW_TRIGGER,
				RANDOMLY_TRIGGERED,
				OnUserRandom$.MODULE$,
				QUESTION_MARK_REPLY,
				OnUserSlashAction$.MODULE$,
				OnInlineQuery$.MODULE$,
				OnCallMe$.MODULE$,
				CALL_MSG_SEND,
				MEDICATION_NOTIFY_APPLY,
				EVENT_HACK_HANDLE
		);
	}
	
}
