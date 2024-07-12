package cc.sukazyo.cono.morny.util

import scala.util.boundary

/** Useful utils of select one specific value in the given values.
  *
  * contains:
  *   - [[select()]] can select one value which is not [[Null]].
  *
  */
object UseSelect {
	
	/** Select the non-null value in the given values.
	  *
	  * @tparam T The value's type.
	  * @param values Given values, may be a T value or [[Null]].
	  * @return The first non-null value in the given values, or [[Null]] if
	  *         there's no non-null value.
	  */
	def select [T] (values: T|Null*): T|Null = {
		boundary[T|Null] {
			for (i <- values) if i != null then boundary.break(i)
			null
		}
	}
	
}
