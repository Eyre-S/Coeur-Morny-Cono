package cc.sukazyo.cono.morny.event_hack

import cc.sukazyo.cono.morny.internal.MornyInternalModule
import cc.sukazyo.cono.morny.MornyCoeur

class ModuleEventHack extends MornyInternalModule {
	
	override val id: String = "morny.event_hack"
	override val name: String = "Morny Event Hack"
	override val description: String | Null =
		// language=markdown
		"""The `/event_hack` command which can make morny output the next
		  |serialized event.
		  |""".stripMargin
	
	override def onInitializingPre (using MornyCoeur)(cxt: MornyCoeur.OnInitializingPreContext): Unit = {
		import cxt.*
		
		given hacker: EventHacker = EventHacker()
		
		externalContext << hacker
		givenCxt << hacker
		
	}
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		given EventHacker = externalContext >!> classOf[EventHacker]
		
		commandManager.register(BotCmdEventHack())
		eventManager.register(BotEventEventHackHandle())
		
	}
	
}
