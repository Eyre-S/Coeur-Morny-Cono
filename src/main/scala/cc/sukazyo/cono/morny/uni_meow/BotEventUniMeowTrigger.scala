package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand

class BotEventUniMeowTrigger (using commands: UniMeowCommandManager) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.*
		
		givenCxt >> { (input: InputCommand) =>
			logger trace s"got input command {$input} from event-context"
			
			for ((name, command_instance) <- commands.commands) {
				logger trace s"checking uni-meow $name"
				if (name == input.command)
					logger trace "checked"
					command_instance.execute(using input, event.update)
					event.setEventOk
			}
			
		} || { logger trace "not command (for uni-meow)" }
		
	}
	
}
