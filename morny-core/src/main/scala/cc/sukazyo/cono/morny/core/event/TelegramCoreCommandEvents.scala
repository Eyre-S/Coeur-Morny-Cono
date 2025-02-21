package cc.sukazyo.cono.morny.core.event

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.messages.MessagingContext
import cc.sukazyo.cono.morny.system.telegram_api.command.ISimpleCommand
import cc.sukazyo.std.event.RichEvent
import cc.sukazyo.std.event.impl.NormalEventManager

class TelegramCoreCommandEvents (using coeur: MornyCoeur) {
	
	val OnUnauthorizedManageCommandCall: RichEvent[(MessagingContext.WithUserAndMessage, ISimpleCommand), Unit] =
		NormalEventManager().initContextWith(initWithCoeur)
	
}

object TelegramCoreCommandEvents {
	
	def inCoeur (using coeur: MornyCoeur): TelegramCoreCommandEvents =
		coeur.telegramCoreCommandEvents
	
}
