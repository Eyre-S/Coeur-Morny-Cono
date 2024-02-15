package cc.sukazyo.cono.morny.core.internal.logging

import cc.sukazyo.cono.morny.core.ServerMain
import cc.sukazyo.cono.morny.util.CommonFormat.formatDate
import cc.sukazyo.messiva.formatter.ILogFormatter
import cc.sukazyo.messiva.log.Log

class MornyFormatterConsole extends ILogFormatter {
	
	override def format (log: Log): String =
		val message = StringBuilder()
		val dt = formatDate(log.timestamp, ServerMain.tz_offset)
		val prompt_heading = s"[$dt][${log.thread.getName}]"
		val prompt_newline = "'" * prompt_heading.length
		val prompt_levelTag = s"${log.level.tag}::: "
		message             ++= prompt_heading ++= prompt_levelTag ++= log.message.message(0)
		for (line <- log.message.message drop 1)
			message += '\n' ++= prompt_newline ++= prompt_levelTag ++= line
		message toString
	
}
