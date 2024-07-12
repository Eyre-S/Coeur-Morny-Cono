package cc.sukazyo.cono.morny.ip186

import cc.sukazyo.cono.morny.core.internal.MornyInternalModule
import cc.sukazyo.cono.morny.core.MornyCoeur

class ModuleIP186 extends MornyInternalModule {
	
	override val id: String = "morny.ext.ip186"
	override val name: String = "Morny ip.186 support"
	override val description: String | Null =
		// language=markdown
		"""Provides `/ip` and `/markdown` commands, using ip.186516.xyz as query backend.
		  |""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		val $command = BotCommand()
		commandManager.register(
			$command.IP,
			$command.Whois
		)
	}
	
}
