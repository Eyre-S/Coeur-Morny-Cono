package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.internal.logging.{MornyFormatterConsole, MornyLoggerBase}
import cc.sukazyo.messiva.appender.ConsoleAppender
import cc.sukazyo.messiva.log.LogLevels

object Log {
	
	val logger: MornyLoggerBase = MornyLoggerBase(
		ConsoleAppender(
			MornyFormatterConsole()
		)
	)
	logger.minLevel(LogLevels.INFO)
	
	def debug: Boolean = logger.levelSetting.minLevel.level <= LogLevels.DEBUG.level
	
	def debug (is: Boolean): Unit =
		if is then logger.minLevel(LogLevels.ALL)
		else logger.minLevel(LogLevels.INFO)
	
}
