package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventEnv
import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.Update

import scala.language.postfixOps

class OnEventHackHandle (using coeur: MornyCoeur) extends EventListener {
	
	override def on (using event: EventEnv): Unit =
		given update: Update = event.update
		var context = EventContext()
		event.consume[EventContext](context = _)
		if coeur.daemons.eventHack.trigger(
			context.chat.map[Long](_.id).getOrElse(0),
			context.invoker.map[Long](_.id).getOrElse(0)
		) then
			event.setEventOk
	
}
