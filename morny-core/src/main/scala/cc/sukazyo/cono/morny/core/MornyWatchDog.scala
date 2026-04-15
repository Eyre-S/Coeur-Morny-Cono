package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.system.utils.EpochDateTime.DurationMillis
import cc.sukazyo.cono.morny.util.time.WatchDog

/** Morny's main WatchDog.
  *
  * It will monitor Morny's healthy during startup stage.
  *
  * The parameters are following parent [[WatchDog]].
  */
class MornyWatchDog (using coeur: MornyCoeur) extends WatchDog {
	
	override def onStart (): Unit = {
		logger.info("WatchDog is monitoring Morny's healthy now!")
	}
	
	override def tick (consumed: DurationMillis): Unit = {
//		logger.trace(s"morny is currently ${coeur.lifecycle.getClass.getSimpleName}")
		coeur.lifecycle match {
			case MornyLifecycleStatus.Starting(thread) =>
//				logger.trace("still starting...")
				if (!thread.isAlive) {
					logger.error("Server is not responding during startup, maybe it has already crashed?")
					logger.info("exiting for the startup failure")
					coeur.exit(65, "startup-failure")
				}
			case _ =>
		}
	}
	
	override def overloaded (consumed: DurationMillis, delayed: DurationMillis): Unit = {
		import cc.sukazyo.cono.morny.util.CommonFormat.formatDuration as f
		logger `warn`
			s"""Can't keep up! is the server overloaded or host machine fall asleep?
			   |  current tick takes ${f(consumed)} to complete.""".stripMargin
		coeur.tasks.notifyIt()
	}
	
}
