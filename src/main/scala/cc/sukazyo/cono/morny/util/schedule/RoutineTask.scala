package cc.sukazyo.cono.morny.util.schedule

import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis

/** The task that can execute multiple times with custom routine function.
  *
  * When creating a Routine Task, the task's [[firstRoutineTimeMillis]] function
  * will be called and the result value will be the first task scheduled time.
  *
  * After every execution complete and enter the post effect, the [[nextRoutineTimeMillis]]
  * function will be called, then its value will be stored as the new task's
  * scheduled time and re-scheduled by its scheduler.
  */
trait RoutineTask extends Task {
	
	private[schedule] var currentScheduledTimeMillis: Option[EpochMillis] = None
	
	/** Next running time of this task.
	  *
	  * Should be auto generated from [[firstRoutineTimeMillis]] when this method
	  * is called at first time, and then from [[nextRoutineTimeMillis]] for following
	  * routines controlled by [[Scheduler]].
	  */
	override def scheduledTimeMillis: EpochMillis =
		currentScheduledTimeMillis match
			case Some(time) => time
			case None =>
				currentScheduledTimeMillis = Some(firstRoutineTimeMillis)
				currentScheduledTimeMillis.get
	
	/** The task scheduled time at initial.
	  *
	  * In the default environment, this function will only be called once
	  * when the task object is just created.
	  */
	def firstRoutineTimeMillis: EpochMillis
	
	/** The function to calculate the next scheduled time after previous task
	  * routine complete.
	  *
	  * This function will be called every time the task is done once, in the
	  * task runner thread and the post effect scope.
	  *
	  * @param previousRoutineScheduledTimeMillis The previous task routine's
	  *                                           scheduled time.
	  * @return The next task routine's scheduled time, or `null` means end
	  *         of the task.
	  */
	def nextRoutineTimeMillis (previousRoutineScheduledTimeMillis: EpochMillis): EpochMillis|Null
	
}
