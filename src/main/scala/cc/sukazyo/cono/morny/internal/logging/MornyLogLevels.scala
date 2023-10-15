package cc.sukazyo.cono.morny.internal.logging

import cc.sukazyo.messiva.log.ILogLevel

enum MornyLogLevels (
	override val level: Float,
	override val tag: String
) extends ILogLevel {
	
	case NOTICE    extends MornyLogLevels(0.2f, "NOTICE")
	case ATTENTION extends MornyLogLevels(0.3f, "ATTION")
	
}
