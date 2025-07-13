package cc.sukazyo.cono.morny.core.module

import cc.sukazyo.cono.morny.core.MornyModule
import cc.sukazyo.cono.morny.util.dataview.Table

object ModuleHelper {
	
	def drawTable (modules: List[MornyModule]): String = {
		Table.format(
			"Module ID" :: "Module Name" :: "Module Version" :: Nil,
			modules.map(f => f.id :: f.name :: f.version :: Nil) *
		)
	}
	
}
