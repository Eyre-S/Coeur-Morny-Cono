package cc.sukazyo.cono.morny.core.internal

import cc.sukazyo.cono.morny.core.{MornyModule, MornySystem}

trait MornyInternalModule extends MornyModule {
	
	override val version: String = MornySystem.VERSION
	
}
