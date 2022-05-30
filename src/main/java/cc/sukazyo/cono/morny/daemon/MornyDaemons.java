package cc.sukazyo.cono.morny.daemon;

import static cc.sukazyo.cono.morny.Log.logger;

public class MornyDaemons {
	
	static MedicationTimer medicationTimerInstance;
	
	public static void start () {
		logger.info("ALL Morny Daemons starting...");
		TrackerDataManager.init();
		medicationTimerInstance = new MedicationTimer();
		medicationTimerInstance.start();
		logger.info("Morny Daemons started.");
	}
	
	public static void stop () {
		
		logger.info("ALL Morny Daemons stopping...");
		
		TrackerDataManager.DAEMON.interrupt();
		medicationTimerInstance.interrupt();
		
		TrackerDataManager.trackingLock.lock();
		try { medicationTimerInstance.join(); } catch (InterruptedException e) { e.printStackTrace(System.out); }
		
		logger.info("ALL Morny Daemons STOPPED.");
		
	}
	
}