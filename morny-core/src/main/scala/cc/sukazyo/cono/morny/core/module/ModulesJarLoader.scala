package cc.sukazyo.cono.morny.core.module

import cc.sukazyo.cono.morny.core.MornyModule
import cc.sukazyo.cono.morny.core.module.ModuleLoader.{loadModuleByNameList, MornyModuleInitializingException}

import java.nio.charset.StandardCharsets

object ModulesJarLoader {
	
	def loadFromJar (packageClazz: Class[?], onLoadingErrors: MornyModuleInitializingException=>Any): List[MornyModule] = {
		val moduleListFile = packageClazz.getResourceAsStream("/morny-modules.list")
			.readAllBytes()
		val modules = String(moduleListFile, StandardCharsets.UTF_8)
			.split("\n")
			.map(_.strip)
			.filter(_.nonEmpty)
		loadModuleByNameList(modules.toList, onLoadingErrors)
	}
	
}
