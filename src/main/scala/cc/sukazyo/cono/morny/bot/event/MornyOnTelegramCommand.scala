package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.command.MornyCommands
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.{Message, Update}

class MornyOnTelegramCommand (using commandManager: MornyCommands) (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		given update: Update = event.update
		
		def _isCommandMessage(message: Message): Boolean =
			if message.text eq null then false
			else if !(message.text startsWith "/") then false
			else if message.text startsWith "/ " then false
			else true
		
		if !_isCommandMessage(update.message) then return
		val inputCommand = InputCommand(update.message.text drop 1)
		event provide inputCommand
		logger trace ":provided InputCommand for event"
		
		if (!(inputCommand.command matches "^\\w+$"))
			logger debug "not command"
		else if ((inputCommand.target ne null) && (inputCommand.target != coeur.username))
			logger debug "not morny command"
		else
			logger debug "is command"
			if commandManager.execute(using inputCommand) then
				event.setEventOk
		
	}
	
}
