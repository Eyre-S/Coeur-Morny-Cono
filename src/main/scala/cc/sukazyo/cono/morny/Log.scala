package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.internal.logging.{MornyFormatterConsole, MornyLoggerBase}
import cc.sukazyo.messiva.appender.ConsoleAppender
import cc.sukazyo.messiva.formatter.SimpleFormatter
import cc.sukazyo.messiva.log.LogLevels
import cc.sukazyo.messiva.logger.Logger

import java.io.{PrintWriter, StringWriter}

object Log {
	
	val logger: MornyLoggerBase = MornyLoggerBase(
		ConsoleAppender(
			MornyFormatterConsole()
		)
	)
	logger minLevel LogLevels.INFO
	
	def debug: Boolean = logger.levelSetting.minLevel.level <= LogLevels.DEBUG.level
	
	def debug(is: Boolean): Unit =
		if is then logger.minLevel(LogLevels.ALL)
		else logger.minLevel(LogLevels.INFO)
	
	def exceptionLog (e: Throwable): String =
		val stackTrace = StringWriter()
		e printStackTrace PrintWriter(stackTrace)
		stackTrace toString
	
}
