package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.internal.logging.{MornyFormatterConsole, MornyLoggerBase}
import cc.sukazyo.messiva.appender.ConsoleAppender
import cc.sukazyo.messiva.log.LogLevels

/** Logger controller of Morny Coeur program */
object Log {
	
	/** The logger that Morny will use */
	val logger: MornyLoggerBase = MornyLoggerBase(
		ConsoleAppender(
			MornyFormatterConsole()
		)
	)
	logger.minLevel(LogLevels.INFO)
	
	/** If the debug level logging is enabled
	  *
	  * @return `true` when the debug logging is enabled, `false` otherwise.
	  */
	def debug: Boolean = logger.levelSetting.minLevel.level <= LogLevels.DEBUG.level
	
	/** Set if the debug logging should be enabled.
	  */
	def debug (is: Boolean): Unit =
		if is then logger.minLevel(LogLevels.ALL)
		else logger.minLevel(LogLevels.INFO)
	
}
