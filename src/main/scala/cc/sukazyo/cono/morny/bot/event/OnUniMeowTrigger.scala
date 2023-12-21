package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.bot.command.MornyCommandManager
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.Log.logger

class OnUniMeowTrigger (using commands: MornyCommandManager) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.*
		
		givenCxt >> { (input: InputCommand) =>
			logger trace s"got input command {$input} from event-context"
			
			for ((name, command_instance) <- commands.commands_uni) {
				logger trace s"checking uni-meow $name"
				if (name == input.command)
					logger trace "checked"
					command_instance.execute(using input, event.update)
					event.setEventOk
			}
			
		} || { logger trace "not command (for uni-meow)" }
		
	}
	
}
