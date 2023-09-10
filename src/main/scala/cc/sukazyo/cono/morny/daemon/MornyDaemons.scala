package cc.sukazyo.cono.morny.daemon

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur

object MornyDaemons {
	
	def start (): Unit = {
		logger info "ALL Morny Daemons starting..."
		//		TrackerDataManager.init();
		MedicationTimer.start()
		MornyReport.onMornyLogin()
		logger info "Morny Daemons started."
		
	}
	
	def stop (): Unit = {
		logger.info("ALL Morny Daemons stopping...")
		//		TrackerDataManager.DAEMON.interrupt();
		MedicationTimer.interrupt()
		//		TrackerDataManager.trackingLock.lock();
		try { MedicationTimer.join() }
		catch case e: InterruptedException =>
			e.printStackTrace(System.out)
		MornyReport.onMornyExit(MornyCoeur.exitReason)
		logger.info("ALL Morny Daemons STOPPED.")
	}
	
}
