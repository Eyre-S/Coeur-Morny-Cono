package cc.sukazyo.cono.morny.encrypt_tool

import cc.sukazyo.cono.morny.core.internal.MornyInternalModule
import cc.sukazyo.cono.morny.core.MornyCoeur

class ModuleEncryptor extends MornyInternalModule {
	
	override val id: String = "morny.encrypt"
	override val name: String = "Morny Encrypt Tools"
	override val description: String | Null =
		// language=markdown
		"""Provides `/encrypt` command for enc/dec/hash things.
		  |""".stripMargin
	
	override def onInitializing (using MornyCoeur)(cxt: MornyCoeur.OnInitializingContext): Unit = {
		import cxt.*
		commandManager register Encryptor()
	}
	
}
