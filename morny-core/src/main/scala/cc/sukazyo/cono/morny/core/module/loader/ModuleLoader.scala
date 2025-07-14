package cc.sukazyo.cono.morny.core.module.loader

import cc.sukazyo.cono.morny.core.module.MornyModule

object ModuleLoader {
	
	/** Errors occurred when in Morny modules load stage.
	  * 
	  * A generic exception that contains module scanning exception, or module initializing
	  * exceptions.
	  * 
	  * @since 2.0.0-alpha21
	  */
	class MornyModuleLoaderException (message: String) extends Exception(message)
	
	/** Errors occurred when running a module scanner to scan modules.
	  * 
	  * @since 2.0.0-alpha21
	  */
	class LookupException (scanner: String) extends Exception(
		s"Failed scan Morny modules for scanner $scanner"
	)
	
	/** Exceptions occurred when initializing a specific module. */
	class MornyModuleInitializingException (val className: String, message: String) extends MornyModuleLoaderException (
		s"Failed to initialize module $className : $message"
	)
	
	/** Trying to load a class that is not a Morny module (not implements [[MornyModule]]) */
	class NotMornyModuleException (className: String) extends MornyModuleInitializingException (
		className,
		s"Class is not a MornyModule, due to it does not implements the ${classOf[MornyModule].getName} trait."
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
	
}
