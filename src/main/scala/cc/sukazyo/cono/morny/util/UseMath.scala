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
	extension (self: Long) {
		@targetName("pow")
		def ** (other: Long): Long =
			var x = 1L
			for (_ <- 0L until other) x *= self
			x
	}
	
	extension (base: Int) {
		def percentageOf (another: Int): Int =
			Math.round((another.toDouble/base)*100).toInt
	}
	
}
