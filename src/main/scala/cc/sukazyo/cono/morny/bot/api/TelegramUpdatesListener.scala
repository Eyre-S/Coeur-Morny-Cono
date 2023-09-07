package cc.sukazyo.cono.morny.bot.api

import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Update

import java.util
import scala.jdk.CollectionConverters.*

object TelegramUpdatesListener extends UpdatesListener {
	
	override def process (updates: util.List[Update]): Int = {
		for (update <- updates.asScala)
			EventListenerManager.publishUpdate(using update)
		UpdatesListener.CONFIRMED_UPDATES_ALL
	}
	
}
