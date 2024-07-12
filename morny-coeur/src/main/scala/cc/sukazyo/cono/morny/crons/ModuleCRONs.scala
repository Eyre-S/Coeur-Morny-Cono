package cc.sukazyo.cono.morny.crons

import cc.sukazyo.cono.morny.core.internal.MornyInternalModule
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.crons.cmd.CommandCreate

class ModuleCRONs extends MornyInternalModule {
	
	override val id: String = "morny.cron"
	override val name: String = "Morny CRON messaging for Users"
	override val description: String | Null =
		// language=markdown
		"""Provides users to set CRON based timer for messaging to themselves.
		  |
		  |Not implemented yet while Morny's database is not implemented yet.""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.commandManager
		
		commandManager `register` CommandCreate()
		
	}
	
}
