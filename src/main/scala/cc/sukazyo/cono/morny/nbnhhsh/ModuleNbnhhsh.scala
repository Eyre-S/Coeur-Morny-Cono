package cc.sukazyo.cono.morny.nbnhhsh

import cc.sukazyo.cono.morny.internal.MornyInternalModule
import cc.sukazyo.cono.morny.MornyCoeur

class ModuleNbnhhsh extends MornyInternalModule {
	
	override val id: String = "morny.nbnhhsh"
	override val name: String = "Morny Nbnhhsh Query"
	override val description: String | Null =
		// language=markdown
		"""Provides a way to translate text using nbnhhsh(能不能好好说话) API.
		  |
		  |- command `/nbnhhsh` for translate input or replied.
		  |- inline query is still under WIP.
		  |
		  |API Url: https://lab.magiconch.com/api/nbnhhsh
		  |""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		commandManager register CommandNbnhhsh()
	}
	
}
