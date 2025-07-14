package cc.sukazyo.cono.morny.core.module.internal

import cc.sukazyo.cono.morny.core.MornySystem
import cc.sukazyo.cono.morny.core.module.MornyModule

trait MornyInternalModule extends MornyModule {
	
	override val version: String = MornySystem.VERSION
	
}
