package cc.sukazyo.cono.morny.util

import scala.language.implicitConversions
import scala.util.Random

/**
  * @todo some tests maybe?
  * @todo use the using clauses to provide random instance
  */
object UseRandom {
	
	class ChancePossibility[T <: Any] (val one: T) (using possibility: Double) {
		def nor[U] (another: U): T|U =
			if Random.nextDouble < possibility then one else another
	}
	
	given Conversion[ChancePossibility[Boolean], Boolean] with
		def apply(in: ChancePossibility[Boolean]): Boolean = in nor !in.one
	
	extension (num: Double) {
		
		def chance_is[T <: Any] (one: T): ChancePossibility[T] =
			ChancePossibility(one)(using num)
		
	}
	
	def rand_half: Boolean = Random.nextBoolean
	
	def rand_id: String =
		import ConvertByteHex.toHex
		Random nextBytes 6 toHex
	
}
