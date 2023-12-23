package cc.sukazyo.cono.morny.daemon

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur

class MornyDaemons (using val coeur: MornyCoeur) {
	
	val reporter: MornyReport = MornyReport()
	
	def start (): Unit = {
		
		logger notice "ALL Morny Daemons starting..."
		
		reporter.start()
		
		logger notice "Morny Daemons started."
		
	}
	
	def stop (): Unit = {
		
		logger notice "stopping All Morny Daemons..."
		
		reporter.stop()
		
		logger notice "stopped ALL Morny Daemons."
	}
	
}
