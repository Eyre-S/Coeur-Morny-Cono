package cc.sukazyo.cono.morny.util.statistics

import scala.annotation.targetName

/** Statistics for numbers.
  *
  * Gives a easy way to get amount of numbers min/max/sum value.
  *
  * Use [[++]] to collect a value to statistics, use [[value]] to
  * get the statistic results.
  *
  * @param role The [[Numeric]] implementation of the given number type,
  *             required for numeric calculation.
  * @tparam T The exactly number type
  */
class NumericStatistics [T] (using role: Numeric[T]) {
	
	/** Statistic state values.
	  *
	  * This class instance should only be used in the statistics manager.
	  * You need to converted it to [[State.Immutable]] version when expose
	  * it (use its [[readonly]] method).
	  *
	  * @param total The sum of all data collected.
	  * @param min The minimal value in the collected data.
	  * @param max The maximize value in the collected data.
	  * @param count total collected data count.
	  */
	class State (
		var min: T,
		var max: T,
		var total: T,
		var count: Int
	) {
		/** Generate the [[State.Immutable]] readonly copy for this. */
		def readonly: State.Immutable = State.Immutable(this)
	}
	object State:
		/** The immutable (readonly) version [[State]]. */
		class Immutable (source: State):
			/** @see [[State.min]] */
			val min: T = source.min
			/** @see [[State.max]] */
			val max: T = source.max
			/** @see [[State.total]] */
			val total: T = source.total
			/** @see [[State.count]] */
			val count: Int = source.count
	
	private var state: Option[State] = None
	
	/** Collect a new data to the statistic.
	  * @return The [[NumericStatistics]] itself for chained call.
	  */
	@targetName("collect")
	def ++ (newOne: T): this.type =
		state match
			case Some(current) =>
				if (role.lt(newOne, current.min)) current.min = newOne
				if (role.gt(newOne, current.max)) current.max = newOne
				current.total = role.plus(current.total, newOne)
				current.count = current.count + 1
			case None =>
				state = Some(new State (
					min = newOne,
					max = newOne,
					total = newOne,
					count = 1
				))
		this
	
	/** Reset the statistics to the initial state.
	  *
	  * All the collected data will be drop.
	  */
	def reset (): Unit =
		state = None
	
	/** Get the statistic values.
	  *
	  * @return An [[Option]] contains one [[State.Immutable]] object
	  *         which refers the statistic state when call this method.
	  *         If the statistic have no data recorded, then it will
	  *         be [[None]]
	  */
	def value: Option[State.Immutable] =
		state match
			case Some(v) => Some(v.readonly)
			case None => None
	
	/** The number counts in the statistics.
	  *
	  * It will always returns a [[Int]] value, regardless if the
	  * statistic is collected some data.
	  */
	def count: Int =
		state match
			case Some(value) => value.count
			case None => 0
	
}
