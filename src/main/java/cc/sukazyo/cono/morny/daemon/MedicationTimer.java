package cc.sukazyo.cono.morny.daemon;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import com.pengrad.telegrambot.request.SendSticker;

import static cc.sukazyo.cono.morny.Log.logger;

public class MedicationTimer extends Thread {
	
	public static final long NOTIFY_RECEIVE_CHAT = 5028252995L;
	
	MedicationTimer () {
		super("TIMER_Medication");
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
				e.printStackTrace(System.out);
			}
		}
		logger.info("MedicationTimer stopped");
	}
	
	private static void sendNotification () {
		MornyCoeur.extra().exec(new SendSticker(NOTIFY_RECEIVE_CHAT, TelegramStickers.ID_PROGYNOVA));
	}
	
	private static long calcNextRoutineTimestamp () {
		return ((System.currentTimeMillis()+8*60*60*1000) / (12*60*60*1000) + 1) * 12*60*60*1000 - 8*60*60*1000;
	}
	
	private void waitToNextRoutine () throws InterruptedException {
		sleep(calcNextRoutineTimestamp() - System.currentTimeMillis());
	}
	
}
