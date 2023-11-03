package cc.sukazyo.cono.morny.internal.schedule

trait DelayedTask (
	val delayedMillis: Long
) extends Task {
	
	override val scheduledTimeMillis: Long = System.currentTimeMillis + delayedMillis
	
}

object DelayedTask {
	
	def apply (_name: String, delayedMillis: Long, task: =>Unit): DelayedTask =
		new DelayedTask (delayedMillis):
			override val name: String = _name
			override def main: Unit = task
	
}
