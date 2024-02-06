package cc.sukazyo.cono.morny.tele_utils.event_hack

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Update.*
import com.pengrad.telegrambot.model.Update

import scala.language.postfixOps

class HackerEventHandler (using hacker: EventHacker)(using coeur: MornyCoeur) extends EventListener {
	
	override def on (using event: EventEnv): Unit =
		given update: Update = event.update
		if hacker.trigger(
			update.extractSourceChat.map[Long](_.id).getOrElse(0),
			update.extractSourceUser.map[Long](_.id).getOrElse(0)
		) then
			event.setEventOk
	
}
