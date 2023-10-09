package cc.sukazyo.cono.morny

import cc.sukazyo.messiva.appender.ConsoleAppender
import cc.sukazyo.messiva.formatter.SimpleFormatter
import cc.sukazyo.messiva.log.LogLevel
import cc.sukazyo.messiva.logger.Logger

import java.io.{PrintWriter, StringWriter}

object Log {
	
	val logger: Logger = Logger(
		ConsoleAppender(
			SimpleFormatter()
		)
	).minLevel(LogLevel.INFO)
	
	def debug: Boolean = logger.levelSetting.minLevel.level <= LogLevel.DEBUG.level
	
	def debug(is: Boolean): Unit =
		if is then logger.minLevel(LogLevel.ALL)
		else logger.minLevel(LogLevel.INFO)
	
	def exceptionLog (e: Throwable): String =
		val stackTrace = StringWriter()
		e printStackTrace PrintWriter(stackTrace)
		stackTrace toString
	
}
