package cc.sukazyo.cono.morny.util.schedule

trait IntervalTask extends RoutineTask {
	
	def intervalMillis: Long
	
	override def firstRoutineTimeMillis: Long =
		System.currentTimeMillis() + intervalMillis
	
	override def nextRoutineTimeMillis (
		previousScheduledRoutineTimeMillis: Long
	): Long|Null =
		previousScheduledRoutineTimeMillis + intervalMillis
	
}

object IntervalTask {
	
	def apply (_name: String, _intervalMillis: Long, task: =>Unit): IntervalTask =
		new IntervalTask:
			override def intervalMillis: Long = _intervalMillis
			override def name: String = _name
			override def main: Unit = task
	
}
