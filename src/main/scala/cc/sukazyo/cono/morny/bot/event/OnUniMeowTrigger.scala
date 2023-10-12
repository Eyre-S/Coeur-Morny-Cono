package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.bot.command.MornyCommands
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import com.pengrad.telegrambot.model.Update

class OnUniMeowTrigger (using commands: MornyCommands) (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		
		event use classOf[InputCommand] consume { input =>
			logger trace s"got input command {$input} from event-context"
			
			for ((name, command_instance) <- commands.commands_uni) {
				logger trace s"checking uni-meow $name"
				if (name == input.command)
					logger trace "checked"
					command_instance.execute(using input, event.update)
					event.setEventOk
			}
			
		} onfail { logger trace "not command (for uni-meow)" }
		
	}
	
}
