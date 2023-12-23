package cc.sukazyo.cono.morny.daemon

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.event_hack.EventHacker

class MornyDaemons (using val coeur: MornyCoeur) {
	
	val medicationTimer: MedicationTimer = MedicationTimer()
	val reporter: MornyReport = MornyReport()
	
	def start (): Unit = {
		
		logger notice "ALL Morny Daemons starting..."
		
		medicationTimer.start()
		reporter.start()
		
		logger notice "Morny Daemons started."
		
	}
	
	def stop (): Unit = {
		
		logger notice "stopping All Morny Daemons..."
		
		medicationTimer.stop()
		reporter.stop()
		
		logger notice "stopped ALL Morny Daemons."
	}
	
}
