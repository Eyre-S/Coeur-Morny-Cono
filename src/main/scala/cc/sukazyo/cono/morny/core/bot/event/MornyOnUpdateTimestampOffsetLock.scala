package cc.sukazyo.cono.morny.core.bot.event

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}

class MornyOnUpdateTimestampOffsetLock (using coeur: MornyCoeur) extends EventListener {
	
	private def checkOutdated (timestamp: Int)(using event: EventEnv): Unit =
		if coeur.config.eventIgnoreOutdated && (timestamp < (coeur.coeurStartTimestamp/1000)) then
			event.setEventCanceled
	
	override def onMessage (using event: EventEnv): Unit = checkOutdated(event.update.message.date)
	override def onEditedMessage (using event: EventEnv): Unit = checkOutdated(event.update.editedMessage.date)
	override def onChannelPost (using event: EventEnv): Unit = checkOutdated(event.update.channelPost.date)
	override def onEditedChannelPost (using event: EventEnv): Unit = checkOutdated(event.update.editedChannelPost.date)
	
}
