package cc.sukazyo.cono.morny.util.time

import cc.sukazyo.cono.morny.util.EpochDateTime.{DurationMillis, EpochMillis}

trait WatchDog (val isDaemonIt: Boolean = true) extends Thread {
	
	val threadName: String = "watch-dog"
	val tickSpeedMillis: DurationMillis = 1000
	val overloadMillis: DurationMillis = tickSpeedMillis + (tickSpeedMillis/2)
	private var previousTickTimeMillis: Option[EpochMillis] = None
	
	this `setName` threadName
	this `setDaemon` isDaemonIt
	
	this.start()
	
	override def run(): Unit = {
		while (!this.isInterrupted) {
			val currentMillis = System.currentTimeMillis()
			previousTickTimeMillis match
				case Some(_previousMillis) =>
					val consumedMillis = currentMillis - _previousMillis
					if consumedMillis > overloadMillis then
						this.overloaded(consumedMillis, consumedMillis - _previousMillis)
					previousTickTimeMillis = Some(currentMillis)
				case _ =>
					previousTickTimeMillis = Some(currentMillis)
			try Thread.sleep(tickSpeedMillis)
			catch case _: InterruptedException =>
				this.interrupt()
		}
	}
	
	def overloaded(consumed: DurationMillis, delayed: DurationMillis): Unit
	
}

object WatchDog {
	
	def apply (
		_threadName: String, _tickSpeedMillis: DurationMillis, _overloadMillis: DurationMillis,
		overloadedCallback: (DurationMillis, DurationMillis) => Unit
	): WatchDog =
		new WatchDog:
			override val threadName: String = _threadName
			override val tickSpeedMillis: DurationMillis = _tickSpeedMillis
			override val overloadMillis: DurationMillis = _overloadMillis
			override def overloaded (consumed: DurationMillis, delayed: DurationMillis): Unit = overloadedCallback(consumed, delayed)
	
	def apply (
		_threadName: String, _tickSpeedMillis: DurationMillis,
		overloadedCallback: (DurationMillis, DurationMillis) => Unit
	): WatchDog =
		new WatchDog:
			override val threadName: String = _threadName
			override val tickSpeedMillis: DurationMillis = _tickSpeedMillis
			override def overloaded (consumed: DurationMillis, delayed: DurationMillis): Unit = overloadedCallback(consumed, delayed)
	
	def apply (
		_threadName: String,
		overloadedCallback: (DurationMillis, DurationMillis) => Unit
	): WatchDog =
		new WatchDog:
			override val threadName: String = _threadName
			override def overloaded (consumed: DurationMillis, delayed: DurationMillis): Unit = overloadedCallback(consumed, delayed)
	
}
