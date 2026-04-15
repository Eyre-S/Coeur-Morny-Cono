package cc.sukazyo.cono.morny.util.time

import cc.sukazyo.cono.morny.system.utils.EpochDateTime.{DurationMillis, EpochMillis}

/** Abstract WatchDog.
  *
  * When a WatchDog is created, it will start a new thread and runs periodically. The period
  * interval is called a "tick". WatchDog's [[threadName thread name]] and
  * [[tickSpeedMillis tick speed]] (along with [[maxTickTimeMillis]] max tick time) can be
  * customized by overriding the corresponding fields.
  *
  * WatchDog thread is a daemon thread by default, so it won't
  * prevent the JVM from exiting.
  *
  * WatchDog will run a tick every [[tickSpeedMillis tick speed]] milliseconds, and calculate
  * the **real time** interval between two ticks. It will also run [[tick tick event]] every
  * tick. After that, if the real time interval is longer than
  * [[maxTickTimeMillis max tick time]], the [[overloaded overloaded event]] will be triggered.
  * All the event method can be overridden to do some works (you should always override at
  * least one of them to make the WatchDog useful).
  *
  * > Tick event and overloaded event are run in the WatchDog's ticking thread, so doing heavy
  * > works may cause the WatchDog itself laggy or stuck.
  * >
  * > The interval is the difference between realtime of last tick complete and realtime of
  * > current tick start, so that event methods are not included in the interval.
  *
  * There are also callback methods for WatchDog's lifecycle, which are [[onStart on start]]
  * and [[onEnd on end]]. They are called when the WatchDog thread starts and ends,
  * respectively. The two methods are also run in the WatchDog's ticking thread.
  */
trait WatchDog (val useDaemonThread: Boolean = true) extends Thread {
	
	/** Thread name of the watch dog thread. Defaults to `watch-dog`. */
	val threadName: String = "watch-dog"
	/** Tick time in milliseconds. Defaults to `1000`. */
	val tickSpeedMillis: DurationMillis = 1000
	/** Max acceptable interval between two ticks. Defaults to 1.5 times of [[tickSpeedMillis]] */
	val maxTickTimeMillis: DurationMillis = tickSpeedMillis + (tickSpeedMillis/2)
	private var previousTickTimeMillis: Option[EpochMillis] = None
	
	this `setName` threadName
	this `setDaemon` useDaemonThread
	
	this.start()
	
	override final def run(): Unit = {
		this.onStart()
		try while (!this.isInterrupted) {
			val currentMillis = System.currentTimeMillis()
			previousTickTimeMillis match
				case Some(_previousMillis) =>
					val consumedMillis = currentMillis - _previousMillis
					this.tick(consumedMillis)
					if consumedMillis > maxTickTimeMillis then
						this.overloaded(consumedMillis, consumedMillis - tickSpeedMillis)
					previousTickTimeMillis = Some(currentMillis)
				case _ =>
					previousTickTimeMillis = Some(currentMillis)
			try Thread.sleep(tickSpeedMillis)
			catch case _: InterruptedException =>
				this.interrupt()
		}
		finally this.onEnd()
	}
	
	/** Event method when the WatchDog thread starts.
	  *
	  * It does nothing by default.
	  *
	  * It calls at the most beginning of ticking thread and runs inside ticking thread.
	  *
	  * This method does not be guard for exceptions, so any uncaught exceptions will cause the
	  * WatchDog thread to stop immediately and the [[onEnd]] event method will not be called
	  * too.
	  */
	protected def onStart (): Unit = ()
	
	/** Event method when the WatchDog threads ends.
	  *
	  * It does nothing by default.
	  *
	  * It calls at the most end of ticking thread and runs inside ticking thread.
	  *
	  * Normally, WatchDog thread will only end when the JVM is exiting or the WatchDog thread
	  * is interrupted. Since JVM won't wait for daemon threads to gracefully end, this method
	  * may only be called when the WatchDog thread is interrupted.
	  *
	  * Another case is when the WatchDog thread occurs an uncaught exception, which will cause
	  * the WatchDog thread to stop immediately. In this case, this method will be called. This
	  * situation normally happens in custom [[tick]] and [[overloaded]] event methods.
	  */
	protected def onEnd (): Unit = ()
	
	/** Tick event method that runs every tick.
	  *
	  * It does nothing by default.
	  *
	  * It runs inside ticking thread. Throwing an uncaught exception in this method will cause
	  * the WatchDog thread to stop immediately and the [[onEnd]] event method will be called.
	  *
	  * It runs every tick even the tick is overloaded (that means inside that tick, both
	  * [[tick]] and [[overloaded]] event methods will be called). But it will not run when the
	  * WatchDog thread is just started (or can be called tick 0), since it needs at least two
	  * ticks to calculate the real time interval between two ticks.
	  *
	  * @param consumed The real time interval between last tick complete and current tick
	  *                 start, in milliseconds.
	  */
	protected def tick (consumed: DurationMillis): Unit = ()
	
	/** Event method when the WatchDog is overloaded.
	  *
	  * A WatchDog is considered overloaded when the real time interval between two ticks is
	  * longer that [[maxTickTimeMillis max tick time]]. The real time interval is the
	  * difference between
	  *
	  * It does nothing by default.
	  *
	  * It runs inside ticking thread. Throwing an uncaught exception in this method will cause
	  * the WatchDog thread to stop immediately and the [[onEnd]] event method will be called.
	  *
	  * @param consumed The real time interval between last tick complete and current tick
	  *                 start, in milliseconds.
	  * @param delayed The real time interval indicates how much the current tick is overloaded,
	  *                in milliseconds. (consumed - [[tickSpeedMillis]])
	  */
	protected def overloaded (consumed: DurationMillis, delayed: DurationMillis): Unit = ()
	
}

object WatchDog {
	
	def apply (
		_threadName: String, _tickSpeedMillis: DurationMillis, _overloadMillis: DurationMillis,
		overloadedCallback: (DurationMillis, DurationMillis) => Unit
	): WatchDog =
		new WatchDog:
			override val threadName: String = _threadName
			override val tickSpeedMillis: DurationMillis = _tickSpeedMillis
			override val maxTickTimeMillis: DurationMillis = _overloadMillis
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
