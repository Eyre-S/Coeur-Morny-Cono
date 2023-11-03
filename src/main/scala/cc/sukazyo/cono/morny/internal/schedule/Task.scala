package cc.sukazyo.cono.morny.internal.schedule

trait Task extends Ordered[Task] {
	
	def name: String
	def scheduledTimeMillis: Long
	
	//noinspection UnitMethodIsParameterless
	def main: Unit
	
	override def compare (that: Task): Int =
		if this.scheduledTimeMillis == that.scheduledTimeMillis then
			this.hashCode - that.hashCode
		else if this.scheduledTimeMillis > that.scheduledTimeMillis then
			1
		else -1
	
	override def toString: String =
		s"""${super.toString}{"$name": $scheduledTimeMillis}"""
	
}
