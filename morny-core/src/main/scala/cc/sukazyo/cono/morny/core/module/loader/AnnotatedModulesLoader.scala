package cc.sukazyo.cono.morny.core.module.loader

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyModule
import cc.sukazyo.cono.morny.core.module.MornyModuleInject
import io.github.classgraph.{ClassGraph, ClassInfo}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Using}

/** Tools that can loads module by scanning the classes that annotated by specific annotations.
  * 
  * @since 2.0.0-alpha21
  */
object AnnotatedModulesLoader {
	
	/** Annotated classes lookup failed.
	  * @since 2.0.0-alpha21
	  */
	class AnnotatedModuleLookupException (anno: Class[?])
		extends ModuleLoader.LookupException(s"annotation ${anno.getSimpleName}")
	
	/** Get Class[T] failed.
	  * @since 2.0.0-alpha21
	  */
	class ModuleClassLoadException (classInfo: ClassInfo)
		extends ModuleLoader.MornyModuleInitializingException(classInfo.getName, "failed load class from its classloader")
	
	/** Load a module from a ClassGraph's [[ClassInfo]].
	  *
	  * The associated [[io.github.classgraph.ScanResult]] of the [[ClassInfo]] must be alive
	  * to be successfully loaded.
	  *
	  * @throws ModuleClassLoadException Exception when load [[Class]] from [[ClassInfo]]
	  * @throws ModuleLoader.MornyModuleInitializingException Exception when initializing the
	  *                                                       instance of the [[Class]]
	  * @return A [[MornyModule]] instance, can be passed to MornyCoeur to load.
	  * 
	  * @since 2.0.0-alpha21
	  */
	@throws[ModuleClassLoadException]
	@throws[ModuleLoader.MornyModuleInitializingException]
	def loadModuleByClassInfo (classInfo: ClassInfo): MornyModule = {
		try
			val clazz = classInfo.loadClass
			ModuleLoader.loadModuleByClass(clazz)
		catch case e: IllegalArgumentException =>
			throw new ModuleClassLoadException(classInfo).initCause(e)
	}
	
	/** @since 2.0.0-alpha21 */
	def scanModules (): List[MornyModule] = {
		
		val annoClazz = classOf[MornyModuleInject]
		val iModClazz = classOf[MornyModule] 
		
		Using (ClassGraph().enableClassInfo().enableAnnotationInfo().scan()) { scanResult =>
			
			val _moduleClasses = ListBuffer[ClassInfo]()
			scanResult.getClassesWithAnnotation(annoClazz).forEach(_moduleClasses += _)
			val moduleClasses = _moduleClasses.toList
			
			val goodOrBadModules = moduleClasses.map(x =>
				if (x.implementsInterface(iModClazz))
					Right(x)
				else Left((x, s"Not implements ${iModClazz.getSimpleName}"))
			)
			val goodModules = goodOrBadModules.filter(_.isRight).map(_.toOption.get)
			val badModules = goodOrBadModules.filter(_.isLeft).map(_.left.toOption.get)
			
			if badModules.nonEmpty then logger.warn((
				"following modules are not available to load, will skip: " ::
				badModules.map(m => s" - ${m._1.getName}, due to ${m._2}")
			).mkString("\n"))
			
			logger.debug((
				s"found modules by ${annoClazz.getSimpleName} lookup: " ::
				goodModules.map(m =>s" - ${m.getName}")
			).mkString("\n"))
			
			val loadedModules = goodModules.map(loadModuleByClassInfo)
			
			loadedModules
			
		} match
			case Success(value) => value
			case Failure(exception) => throw AnnotatedModuleLookupException(annoClazz).initCause(exception)
		
	}
	
	
}
