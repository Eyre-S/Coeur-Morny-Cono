package cc.sukazyo.cono.morny.util.schedule

trait RoutineTask extends Task {
	
	private[schedule] var currentScheduledTimeMillis: Long = firstRoutineTimeMillis
	override def scheduledTimeMillis: Long = currentScheduledTimeMillis
	
	def firstRoutineTimeMillis: Long
	
	def nextRoutineTimeMillis (previousRoutineScheduledTimeMillis: Long): Long|Null
	
}
