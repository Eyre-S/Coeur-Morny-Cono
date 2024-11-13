package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.util.CommonFormat

import java.time.Duration
import java.time.temporal.ChronoUnit

@main def MornyCLI (): Unit = {
	
	val duration = Duration.ZERO
//		.plusDays(2)
//		.plusHours(21)
//		.plusMinutes(2)
//		.plusSeconds(5)
		.plusMillis(123)
		.plusNanos(876548)
	
	val echo = CommonFormat.formatDurationTimers(duration, ChronoUnit.SECONDS)
	
	println(echo)
	
}
