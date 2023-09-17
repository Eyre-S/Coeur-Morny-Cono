package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.command.MornyCommands
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.{Message, Update}

object OnTelegramCommand extends EventListener {
	
	override def onMessage (using update: Update): Boolean = {
		
		def _isCommandMessage(message: Message): Boolean =
			if message.text eq null then false
			else if !(message.text startsWith "/") then false
			else if message.text startsWith "/ " then false
			else true
		
		if !_isCommandMessage(update.message) then return false
		val inputCommand = InputCommand(update.message.text drop 1)
		if (!(inputCommand.command matches "^\\w+$"))
			logger debug "not command"
			false
		else if ((inputCommand.target ne null) && (inputCommand.target != MornyCoeur.username))
			logger debug "not morny command"
			false
		else
			logger debug "is command"
			MornyCommands.execute(using inputCommand)
		
	}
	
}
