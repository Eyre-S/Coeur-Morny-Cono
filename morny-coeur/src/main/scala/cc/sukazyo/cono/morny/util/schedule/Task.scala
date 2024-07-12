package cc.sukazyo.cono.morny.util.schedule

import cc.sukazyo.cono.morny.system.utils.EpochDateTime.EpochMillis

/** A schedule task that can be added to [[Scheduler]].
  *
  * Contains some basic task information: [[name]], [[scheduledTimeMillis]],
  * and [[main]] as the method which will be called.
  *
  * Tasks are ordered by time, and makes sure that two different task instance
  * is NOT THE SAME.
  * <blockquote>
  *     When comparing two tasks, it will firstly compare the [[scheduledTimeMillis]]:
  *     If the result is the not the same, return it; If the result is the same, then
  *     using [[Object]]'s compare method to compare it.
  * </blockquote>
  */
trait Task extends Ordered[Task] {
	
	/** Task name. Also the executor thread name when task is executing.
	  *
	  * Will be used in [[Scheduler]] to change the running thread's name.
	  */
	def name: String
	/** Next running time.
	  *
	  * If it is smaller than current time, the task should be executed immediately.
	  */
	def scheduledTimeMillis: EpochMillis
	
	//noinspection UnitMethodIsParameterless
	def main: Unit
	
	override def compare (that: Task): Int =
		scheduledTimeMillis.compareTo(that.scheduledTimeMillis) match
			case 0 => this.hashCode - that.hashCode
			case n => n
	
	/** Returns this task's object name and the task name.
	  *
	  * @example {{{
	  *     scala> val task = new Task {
	  *         val name = "example-task"
	  *         val scheduledTimeMillis = 0
	  *         def main = println("example")
	  *     }
	  *     val task: cc.sukazyo.cono.morny.util.schedule.Task = anon$1@26d8908e{"example-task": 0}
	  *
	  *     scala> task.toString
	  *     val res0: String = anon$1@26d8908e{"example-task": 0}
	  * }}}
	  */
	override def toString: String =
		s"""${super.toString}{"$name": $scheduledTimeMillis}"""
	
}

object Task {
	
	def apply (_name: String, _scheduledTime: EpochMillis, _main: =>Unit): Task =
		new Task:
			override def name: String = _name
			override def scheduledTimeMillis: EpochMillis = _scheduledTime
			override def main: Unit = _main
	
}
