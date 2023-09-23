package cc.sukazyo.cono.morny.bot.api

import cc.sukazyo.cono.morny.MornyCoeur
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Update

import java.util
import scala.jdk.CollectionConverters.*

class TelegramUpdatesListener (using MornyCoeur) extends UpdatesListener {
	
	val manager = EventListenerManager()
	
	override def process (updates: util.List[Update]): Int = {
		for (update <- updates.asScala)
			manager.publishUpdate(using update)
		UpdatesListener.CONFIRMED_UPDATES_ALL
	}
	
}
