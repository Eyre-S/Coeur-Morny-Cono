package cc.sukazyo.cono.morny.internal.logging

import cc.sukazyo.messiva.appender.IAppender
import cc.sukazyo.messiva.log.{Log, Message}
import cc.sukazyo.messiva.logger.Logger

class MornyLoggerBase extends Logger with IMornyLogLevelImpl {
	
	def this (appends: IAppender*) =
		this()
		this.appends.addAll(java.util.List.of(appends:_*))
	
	override def notice (message: String): Unit =
		pushToAllAppender(Log(1, new Message(message), MornyLogLevels.NOTICE))
	override def notice (message: Message): Unit =
		pushToAllAppender(Log(1, message, MornyLogLevels.NOTICE))
	override def attention (message: String): Unit =
		pushToAllAppender(Log(1, new Message(message), MornyLogLevels.ATTENTION))
	override def attention (message: Message): Unit =
		pushToAllAppender(Log(1, message, MornyLogLevels.ATTENTION))
	
}
