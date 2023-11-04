package cc.sukazyo.cono.morny.util.schedule

trait IntervalWithTimesTask extends IntervalTask {
	
	def times: Int
	private var currentExecutedTimes = 1
	
	override def nextRoutineTimeMillis (previousScheduledRoutineTimeMillis: Long): Long | Null =
		if currentExecutedTimes >= times then
			null
		else
			currentExecutedTimes = currentExecutedTimes + 1
			super.nextRoutineTimeMillis(previousScheduledRoutineTimeMillis)
	
}

object IntervalWithTimesTask {
	
	def apply (_name: String, _intervalMillis: Long, _times: Int, task: =>Unit): IntervalWithTimesTask =
		new IntervalWithTimesTask:
			override def name: String = _name
			override def times: Int = _times
			override def intervalMillis: Long = _intervalMillis
			override def main: Unit = task
	
}
