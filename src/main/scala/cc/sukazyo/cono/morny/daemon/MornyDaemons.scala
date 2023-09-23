package cc.sukazyo.cono.morny.daemon

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur

class MornyDaemons (using val coeur: MornyCoeur) {
	
	val medicationTimer: MedicationTimer = MedicationTimer()
	val reporter: MornyReport = MornyReport()
	val eventHack: EventHacker = EventHacker()
	
	def start (): Unit = {
		logger info "ALL Morny Daemons starting..."
		//		TrackerDataManager.init();
		medicationTimer.start()
		logger info "Morny Daemons started."
		
	}
	
	def stop (): Unit = {
		logger.info("stopping All Morny Daemons...")
		//		TrackerDataManager.DAEMON.interrupt();
		medicationTimer.interrupt()
		//		TrackerDataManager.trackingLock.lock();
		try { medicationTimer.join() }
		catch case e: InterruptedException =>
			e.printStackTrace(System.out)
		logger.info("stopped ALL Morny Daemons.")
	}
	
}
