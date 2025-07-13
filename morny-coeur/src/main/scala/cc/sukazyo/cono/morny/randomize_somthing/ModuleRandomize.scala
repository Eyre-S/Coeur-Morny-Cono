package cc.sukazyo.cono.morny.randomize_somthing

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.module.internal.MornyInternalModule
import cc.sukazyo.cono.morny.core.module.MornyModuleInject

@MornyModuleInject
class ModuleRandomize extends MornyInternalModule {
	
	override val id: String = "morny.rand"
	override val name: String = "Morny Randomize Something"
	override val description: String | Null =
		// language=markdown
		"""Randomize reply something by rand.
		  |
		  |Can randomly reply like *尊嘟假嘟*, and provide support for */ $this or $that*,
		  |and more interesting things.
		  |""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		
		val $OnUserRandom = OnUserRandom()
		eventManager register $OnUserRandom.RandomSelect
		eventManager register OnQuestionMarkReply()
		//noinspection NonAsciiCharacters
		eventManager register $OnUserRandom.尊嘟假嘟
		
	}
	
}
