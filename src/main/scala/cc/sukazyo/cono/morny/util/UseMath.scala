package cc.sukazyo.cono.morny.util

import scala.annotation.targetName

/** @todo some tests */
object UseMath {
	
	extension (self: Int) {
		
		infix def over (other: Int): Double = self.toDouble / other
		
	}
	
	extension (self: Int) {
		@targetName("pow")
		def ** (other: Int): Double = Math.pow(self, other)
	}
	
	extension (base: Int) {
		infix def percentageOf (another: Int): Int =
			Math.round((another.toDouble/base)*100).toInt
	}
	
}
