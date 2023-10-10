package cc.sukazyo.cono.morny.util

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object EpochDateTime {
	
	type EpochMillis = Long
	
	object EpochMillis:
		/** convert a localtime with timezone to epoch milliseconds
		  *
		  * @param time the local time in that timezone, should be formatted
		  *             in [[DateTimeFormatter.ISO_DATE_TIME]]
		  * @param zone timezone of the localtime.
		  *             cannot be "UTC" or "GMT" (use "Z" instead)
		  * @return the epoch millisecond the local time means.
		  */
		def apply (time: String, zone: String): EpochMillis = {
			val formatter = DateTimeFormatter.ISO_DATE_TIME
			val innerTime = LocalDateTime.parse(time, formatter)
			val instant = innerTime.toInstant(ZoneOffset of zone)
			instant.toEpochMilli
		}
		def apply (time_zone: (String, String)): EpochMillis =
			time_zone match
				case (time, zone) => apply(time, zone)
	
}
