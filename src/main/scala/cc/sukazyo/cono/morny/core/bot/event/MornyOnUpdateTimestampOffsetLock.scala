package cc.sukazyo.cono.morny.core.bot.event

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Update.sourceTime
import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis

class MornyOnUpdateTimestampOffsetLock (using coeur: MornyCoeur) extends EventListener {
	
	override def executeFilter (using env: EventEnv): Boolean =
		if (
			(env.update.message != null) ||
			(env.update.editedMessage != null) ||
			(env.update.channelPost != null) ||
			(env.update.editedChannelPost != null)
		)
			true
		else false
	
	override def on (using event: EventEnv): Unit =
		event.update.sourceTime match
			case Some(timestamp) =>
				if coeur.config.eventIgnoreOutdated && (EpochMillis.fromEpochSeconds(timestamp) < coeur.coeurStartTimestamp) then
					event.setEventCanceled
			case None =>
	
}
