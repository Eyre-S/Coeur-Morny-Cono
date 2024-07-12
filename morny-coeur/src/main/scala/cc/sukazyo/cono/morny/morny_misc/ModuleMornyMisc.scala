package cc.sukazyo.cono.morny.morny_misc

import cc.sukazyo.cono.morny.core.internal.MornyInternalModule
import cc.sukazyo.cono.morny.core.MornyCoeur

class ModuleMornyMisc extends MornyInternalModule {
	
	override val id: String = "morny.misc"
	override val name: String = "Morny Misc Things"
	override val description: String | Null = "Misc things that from old days Morny."
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		
		commandManager register MornyOldJrrp()
		commandManager register Testing()
		
	}
	
}
