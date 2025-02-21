package cc.sukazyo.cono.morny.core.module

import cc.sukazyo.cono.morny.core.{MornyCoeur, MornyModule}
import cc.sukazyo.cono.morny.core.module.ModuleLoader.MornyModuleInitializingException
import cc.sukazyo.cono.morny.core.module.ModulesJarLoader.loadFromJar
import cc.sukazyo.cono.morny.util.dataview.Table

object ModuleHelper {
	
	def drawTable (modules: List[MornyModule]): String = {
		Table.format(
			"Module ID" :: "Module Name" :: "Module Version" :: Nil,
			modules.map(f => f.id :: f.name :: f.version :: Nil) *
		)
	}
	
	def loadCoeurModules (onLoadingErrors: MornyModuleInitializingException =>Any): List[MornyModule] = {
		loadFromJar(classOf[MornyCoeur], onLoadingErrors)
	}
	
}
