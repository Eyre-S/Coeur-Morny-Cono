package cc.sukazyo.cono.morny.tele_utils.event_hack

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Update.*
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener}
import com.pengrad.telegrambot.model.Update

import scala.language.postfixOps

class HackerEventHandler (using hacker: EventHacker)(using coeur: MornyCoeur) extends EventListener {
	
	override def on (using event: EventEnv): Unit =
		given update: Update = event.update
		if hacker.trigger(
			update.sourceChat.map[Long](_.id).getOrElse(0),
			update.sourceUser.map[Long](_.id).getOrElse(0)
		) then
			event.setEventOk
	
}
