package cc.sukazyo.cono.morny.call_me

import cc.sukazyo.cono.morny.internal.MornyInternalModule
import cc.sukazyo.cono.morny.MornyCoeur

class ModuleCallMe extends MornyInternalModule {
	
	override val id: String = "morny.call_me"
	override val name: String = "Morny Can Call Master"
	override val description: String | Null =
		"""Provides a serial private message handler that can talk with Morny's Master
		  |inside the Morny bot PM.""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		
		eventManager register OnCallMe()
		eventManager register OnCallMsgSend()
		
	}
	
}
