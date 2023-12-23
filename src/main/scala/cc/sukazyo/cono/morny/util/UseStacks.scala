package cc.sukazyo.cono.morny.util

import cc.sukazyo.messiva.utils.StackUtils

import scala.reflect.{classTag, ClassTag}
import scala.util.boundary

object UseStacks {
	
	def getStackHeadBeforeClass[T: ClassTag]: StackTraceElement = {
		boundary {
			for (stack <- StackUtils.getStackTrace(1)) {
				if (!stack.getClassName.startsWith(classTag[T].runtimeClass.getName))
					boundary break stack
			}
			StackTraceElement("unknown", "unknown", "unknown", -1)
		}
	}
	
}
