package cc.sukazyo.cono.morny.daemon;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.util.CommonFormat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cc.sukazyo.cono.morny.Log.exceptionLog;
import static cc.sukazyo.cono.morny.Log.logger;

public class MedicationTimer extends Thread {
	
	public static final ZoneOffset USE_TIME_ZONE = ZoneOffset.ofHours(8);
	public static final Set<Integer> NOTIFY_AT_HOUR = Set.of(12);
	public static final long NOTIFY_CHAT = -1001729016815L;
	public static final String NOTIFY_MESSAGE = "\uD83C\uDF65⏲";
	private static final String DAEMON_THREAD_NAME = "TIMER_Medication";
	
	private static final long LAST_NOTIFY_ID_NULL = -1L;
	private long lastNotify = LAST_NOTIFY_ID_NULL;
	
	
	MedicationTimer () {
		super(DAEMON_THREAD_NAME);
	}
	
	@Override
	public void run () {
		logger.info("MedicationTimer started");
		while (!interrupted()) {
			try {
				waitToNextRoutine();
				sendNotification();
			} catch (InterruptedException e) {
				interrupt();
				logger.info("MedicationTimer was interrupted, will be exit now");
			} catch (Exception e) {
				logger.error("Unexpected error occurred");
				logger.error(exceptionLog(e));
				MornyReport.exception(e);
			}
		}
		logger.info("MedicationTimer stopped");
	}
	
	private void sendNotification () {
		final SendResponse resp = MornyCoeur.extra().exec(new SendMessage(NOTIFY_CHAT, NOTIFY_MESSAGE));
		if (resp.isOk()) lastNotify = resp.message().messageId();
		else lastNotify = LAST_NOTIFY_ID_NULL;
	}
	
	public void refreshNotificationWrite (Message edited) {
		if (edited.messageId() != lastNotify) return;
		final String editTime = CommonFormat.formatDate(edited.editDate()*1000, 8);
		ArrayList<MessageEntity> entities = new ArrayList<>();
		if (edited.entities() != null) entities.addAll(List.of(edited.entities()));
		entities.add(new MessageEntity(MessageEntity.Type.italic, edited.text().length() + "\n-- ".length(), editTime.length()));
		EditMessageText sending = new EditMessageText(
				NOTIFY_CHAT,
				edited.messageId(),
				edited.text() + "\n-- " + editTime + " --"
		).parseMode(ParseMode.HTML).entities(entities.toArray(MessageEntity[]::new));
		MornyCoeur.extra().exec(sending);
		lastNotify = LAST_NOTIFY_ID_NULL;
	}
	
	public static long calcNextRoutineTimestamp (long baseTimeMillis, ZoneOffset useTimeZone, Set<Integer> atHours) {
		LocalDateTime time = LocalDateTime.ofEpochSecond(
				baseTimeMillis/1000, (int)baseTimeMillis%1000*1000,
				useTimeZone
		).withMinute(0).withSecond(0).withNano(0);
		do {
			time = time.plusHours(1);
		} while (!atHours.contains(time.getHour()));
		return time.withMinute(0).withSecond(0).withNano(0).toInstant(useTimeZone).toEpochMilli();
	}
	
	private void waitToNextRoutine () throws InterruptedException {
		sleep(calcNextRoutineTimestamp(System.currentTimeMillis(), USE_TIME_ZONE, NOTIFY_AT_HOUR) - System.currentTimeMillis());
	}
	
}
