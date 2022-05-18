package cc.sukazyo.cono.morny.daemon;

import static cc.sukazyo.cono.morny.Log.logger;

public class MornyDaemons {
	
	static MedicationTimer medicationTimerInstance;
	
	public static void start () {
		logger.info("ALL Morny Daemons starting...");
		TrackerDataManager.init();
		medicationTimerInstance = new MedicationTimer();
		logger.info("Morny Daemons started.");
	}
	
	public static void stop () {
		
		logger.info("ALL Morny Daemons stopping...");
		
		TrackerDataManager.DAEMON.interrupt();
		TrackerDataManager.trackingLock.lock();
		
		logger.info("Morny Daemons stopped.");
		
	}
	
}
