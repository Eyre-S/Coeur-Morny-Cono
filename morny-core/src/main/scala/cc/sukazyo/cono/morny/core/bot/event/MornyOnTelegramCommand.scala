package cc.sukazyo.cono.morny.core.bot.event

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.MornyCommandManager
import cc.sukazyo.cono.morny.system.telegram_api.command.InputCommand
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener}
import com.pengrad.telegrambot.model.{Message, Update}

class MornyOnTelegramCommand (using commandManager: MornyCommandManager) (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.*
		given Update = update
		
		def _isCommandMessage(message: Message): Boolean =
			if message.text eq null then false
			else if !(message.text `startsWith` "/") then false
			else if message.text `startsWith` "/ " then false
			else true
		
		if !_isCommandMessage(update.message) then return
		val inputCommand = InputCommand(update.message.text drop 1)
		givenCxt << inputCommand
		logger `trace` ":provided InputCommand for event"
		
		if (!(inputCommand.command `matches` "^\\w+$"))
			logger `debug` "not command"
		else if ((inputCommand.target ne null) && (inputCommand.target != coeur.username))
			logger `debug` "not morny command"
		else
			logger `debug` "is command"
			if commandManager.execute(using inputCommand) then
				setEventOk
		
	}
	
}
