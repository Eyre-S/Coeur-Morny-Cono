package cc.sukazyo.cono.morny.util

import scala.annotation.targetName

/** @todo some tests */
object UseMath {
	
	extension (self: Int) {
		
		def over (other: Int): Double = self.toDouble / other
		
	}
	
	extension (self: Int) {
		@targetName("pow")
		def ** (other: Int): Double = Math.pow(self, other)
	}
	
}
