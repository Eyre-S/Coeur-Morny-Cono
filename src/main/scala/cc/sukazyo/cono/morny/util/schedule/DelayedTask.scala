package cc.sukazyo.cono.morny.util.schedule

import cc.sukazyo.cono.morny.util.EpochDateTime.{DurationMillis, EpochMillis}

trait DelayedTask (
	val delayedMillis: DurationMillis
) extends Task {
	
	override val scheduledTimeMillis: EpochMillis = System.currentTimeMillis + delayedMillis
	
}

object DelayedTask {
	
	def apply (_name: String, delayedMillis: DurationMillis, task: =>Unit): DelayedTask =
		new DelayedTask (delayedMillis):
			override val name: String = _name
			override def main: Unit = task
	
}
