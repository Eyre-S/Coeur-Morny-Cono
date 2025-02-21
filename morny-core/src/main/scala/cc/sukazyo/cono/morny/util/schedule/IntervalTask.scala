package cc.sukazyo.cono.morny.util.schedule

import cc.sukazyo.cono.morny.system.utils.EpochDateTime.{DurationMillis, EpochMillis}

trait IntervalTask extends RoutineTask {
	
	def intervalMillis: DurationMillis
	
	override def firstRoutineTimeMillis: EpochMillis =
		System.currentTimeMillis() + intervalMillis
	
	override def nextRoutineTimeMillis (
		previousScheduledRoutineTimeMillis: EpochMillis
	): EpochMillis|Null =
		previousScheduledRoutineTimeMillis + intervalMillis
	
}

object IntervalTask {
	
	def apply (_name: String, _intervalMillis: DurationMillis, task: =>Unit): IntervalTask =
		new IntervalTask:
			override def intervalMillis: DurationMillis = _intervalMillis
			override def name: String = _name
			override def main: Unit = task
	
}
