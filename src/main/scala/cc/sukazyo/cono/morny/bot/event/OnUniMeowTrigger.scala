package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.bot.command.MornyCommands
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update

object OnUniMeowTrigger extends EventListener {
	
	override def onMessage (using update: Update): Boolean = {
		
		if update.message.text eq null then return false
		var ok = false
		for ((name, command) <- MornyCommands.commands_uni)
			val _name = "/"+name
			if (_name == update.message.text)
				command.execute(using InputCommand(_name))
				ok = true
		ok
		
	}
	
}
