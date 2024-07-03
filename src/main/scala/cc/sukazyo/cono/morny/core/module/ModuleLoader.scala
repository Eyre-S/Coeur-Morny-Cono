package cc.sukazyo.cono.morny.core.module

import cc.sukazyo.cono.morny.core.{MornyCoeur, MornyModule}
import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString

import java.nio.charset.StandardCharsets
import scala.collection.mutable.ListBuffer

object ModuleLoader {
	
	def loadCoreModules (): List[MornyModule] = {
		loadFromJar(classOf[MornyCoeur])
	}
	
	def loadFromJar (packageClazz: Class[?]): List[MornyModule] = {
		val list = ListBuffer[MornyModule]()
		
		val moduleListFile = packageClazz.getResourceAsStream("/morny-modules.list")
			.readAllBytes()
		val modules = String(moduleListFile, StandardCharsets.UTF_8)
			.split("\n")
			.map(_.strip)
			.filter(_.nonEmpty)
		
		modules.foreach { (clazzName: String) =>
			try {
				val clazz = Class.forName(clazzName)
				val instance = clazz.getConstructor().newInstance()
				instance match
					case module: MornyModule =>
						list += module
					case _ =>
						logger `error`
							s"""Module is not a Morny Module :
							   | - in package : ${packageClazz.getName}
							   | - declared class name : $clazzName
							   |You need to implement a MornyModule trait to make it a REAL morny module!""".stripMargin
			} catch case e: Exception =>
				logger `error`
					s"""Failed to create a module instance :
					   | - in package : ${packageClazz.getName}
					   | - declared class name : $clazzName
					   |${e.toLogString}
					   |Is this a typo or packaging error? You need to add morny-modules.list and your code to the same jar.""".stripMargin
		}
		
		list.toList
	}
	
}
