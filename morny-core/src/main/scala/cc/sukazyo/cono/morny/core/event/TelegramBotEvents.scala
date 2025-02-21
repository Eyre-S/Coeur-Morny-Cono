package cc.sukazyo.cono.morny.core.event

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener as TelegramEventListener}
import cc.sukazyo.std.event.RichEvent
import cc.sukazyo.std.event.impl.NormalEventManager
import com.pengrad.telegrambot.TelegramException

class TelegramBotEvents (using coeur: MornyCoeur) {
	
	/**
	  * Event: OnGetUpdateFailed in TelegramBotEvents
	  *
	  * This event will be emitted when an exception occurred when the Telegram Bot is trying
	  * to execute the GetUpdate.
	  *
	  * Provides a [[TelegramException]] that contains the exception information.
	  *
	  * Event is initialized after the [[MornyModule.onStarting]] stage, and before the
	  * [[MornyModule.onStartingPost]] stage.
	  * You should register your own listener at stage [[MornyModule.onStartingPost]].
	  */
	val OnGetUpdateFailed: NormalEventManager[TelegramException, Unit] =
		NormalEventManager().initContextWith(initWithCoeur)
	
	val OnListenerOccursException: RichEvent[(Throwable, TelegramEventListener, EventEnv), Unit] =
		NormalEventManager().initContextWith(initWithCoeur)
	
}

object TelegramBotEvents {
	
	def inCoeur (using coeur: MornyCoeur): TelegramBotEvents = in(coeur)
	def in (coeur: MornyCoeur): TelegramBotEvents =
		coeur.telegramBotEvents
	
}
