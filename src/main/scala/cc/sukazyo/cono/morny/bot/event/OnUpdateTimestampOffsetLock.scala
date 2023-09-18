package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.MornyCoeur
import com.pengrad.telegrambot.model.Update

object OnUpdateTimestampOffsetLock extends EventListener {
	
	private def isOutdated (timestamp: Int): Boolean =
		timestamp < (MornyCoeur.config.eventOutdatedTimestamp/1000)
	
	override def onMessage (using update: Update): Boolean = isOutdated(update.message.date)
	override def onEditedMessage (using update: Update): Boolean = isOutdated(update.editedMessage.date)
	override def onChannelPost (using update: Update): Boolean = isOutdated(update.channelPost.date)
	override def onEditedChannelPost (using update: Update): Boolean = isOutdated(update.editedChannelPost.date)
	
}
