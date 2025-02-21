package cc.sukazyo.cono.morny.core.module

import cc.sukazyo.cono.morny.core.MornyModule

import scala.collection.mutable.ListBuffer

object ModuleLoader {
	
	class MornyModuleInitializingException (val className: String, message: String) extends Exception (
		s"Failed to initialize module $className : $message"
	)
	
	class NotMornyModuleException (className: String) extends MornyModuleInitializingException (
		className,
		s"Class is not a MornyModule, due to it does not implements the cc.sukazyo.cono.morny.core.MornyModule trait."
	)
	
	class MornyModuleNotFoundException (className: String) extends MornyModuleInitializingException (
		className,
		s"Cannot found class in this name."
	)
	
	@throws[MornyModuleInitializingException]
	def loadModuleByClass (clazz: Class[?]): MornyModule = {
		try {
			val instance = clazz.getConstructor().newInstance()
			instance match
				case module: MornyModule =>
					module
				case _ =>
					throw NotMornyModuleException(clazz.getName)
		} catch {
			case e_module: MornyModuleInitializingException =>
				throw e_module
			case e_any: Exception =>
				throw MornyModuleInitializingException(clazz.getName, e_any.getMessage).initCause(e_any)
		}
	}
	
	@throws[MornyModuleInitializingException]
	def loadModuleByClassName (className: String): MornyModule = {
		try {
			val clazz = Class.forName(className)
			loadModuleByClass(clazz)
		} catch {
			case e_module: MornyModuleInitializingException =>
				throw e_module
			case e_notFound: ClassNotFoundException =>
				throw MornyModuleNotFoundException(className).initCause(e_notFound)
		}
	}
	
	def loadModuleByNameList (moduleClassNames: List[String], onLoadingErrors: MornyModuleInitializingException =>Any): List[MornyModule] = {
		
		val list = ListBuffer[MornyModule]()
		
		moduleClassNames.foreach { (clazzName: String) =>
			try {
				val module = loadModuleByClassName(clazzName)
				list += module
			} catch case e: MornyModuleInitializingException =>
				onLoadingErrors(e)
		}
		
		list.toList
		
	}
	
}
