package cc.sukazyo.cono.morny.util

import scala.collection.mutable.ListBuffer

object UseException {
	
	/** Get all the exception caused to the list, include itself. 
	  * 
	  * @param throwable An exception.
	  * @return The exception itself and all the following caused,
	  *         in order like [ex, ex.caused, ex.caused.caused, ...]
	  */
	def getCauseStack (throwable: Throwable): List[Throwable] = {
		val buffer = new ListBuffer[Throwable]()
		var stack: Throwable|Null = throwable
		while (stack != null) {
			buffer.append(stack)
			stack = stack.getCause
		}
		buffer.toList
	}
	
}
