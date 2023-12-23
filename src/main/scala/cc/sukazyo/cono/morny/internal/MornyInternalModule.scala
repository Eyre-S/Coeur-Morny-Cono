package cc.sukazyo.cono.morny.internal

import cc.sukazyo.cono.morny.{MornyModule, MornySystem}

trait MornyInternalModule extends MornyModule {
	
	override val version: String = MornySystem.VERSION
	
}
