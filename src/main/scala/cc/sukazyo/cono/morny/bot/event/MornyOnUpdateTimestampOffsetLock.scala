package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.event.MornyOnUpdateTimestampOffsetLock.ExpiredEvent

class MornyOnUpdateTimestampOffsetLock (using coeur: MornyCoeur) extends EventListener {
	
	override def on (using event: EventEnv): Unit = {
		event.consume[EventContext] { context =>
			context.timestamp match
				case Some(timestamp) =>
					if timestamp < (coeur.coeurStartTimestamp / 1000) then {
						event.provide(ExpiredEvent)
						if coeur.config.eventIgnoreOutdated then
							event.setEventCanceled
					}
				case _ =>
		}
	}
	
}

object MornyOnUpdateTimestampOffsetLock {
	object ExpiredEvent
}
