package cc.sukazyo.cono.morny.util

import java.io.{PrintWriter, StringWriter}

object UseThrowable {
	
	extension (t: Throwable) {
		
		def toLogString: String =
			val stackTrace = StringWriter()
			t `printStackTrace` PrintWriter(stackTrace)
			stackTrace toString
		
	}
	
}
