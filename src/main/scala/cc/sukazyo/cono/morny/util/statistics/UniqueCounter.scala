package cc.sukazyo.cono.morny.util.statistics

import scala.collection.mutable

/** Count unique elements progressively.
  * 
  * Use [[<<]] to add an element to this counter. Use [[count]] to get current
  * count in this counter, and use [[reset()]] to reset this counter.
  * 
  * Behind it is a [[scala.collection.mutable.Set]].
  * 
  * @tparam T The element type.
  */
class UniqueCounter [T] {
	
	private val set: mutable.Set[T] = mutable.Set.empty
	
	def << (t: T): Unit =
		set += t
	
	def count: Int =
		set.size
	
	def reset(): Unit =
		set.clear()
	
}
