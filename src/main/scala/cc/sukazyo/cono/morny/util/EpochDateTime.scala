package cc.sukazyo.cono.morny.util

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

object EpochDateTime {
	
	/** The UNIX Epoch Time in milliseconds.
	  *
	  * aka. Milliseconds since 00:00:00 UTC on Thursday, 1 January 1970.
	  */
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
		
		/** Convert from [[EpochSeconds]].
		  *
		  * Due to the missing accuracy, the converted EpochMillis will
		  * be always in 0ms aligned.
		  */
		def fromEpochSeconds (epochSeconds: EpochSeconds): EpochMillis =
			epochSeconds.longValue * 1000L
	
	/** The UNIX Epoch Time in seconds.
	  *
	  * aka. Seconds since 00:00:00 UTC on Thursday, 1 January 1970.
	  *
	  * Normally is the epochSeconds = (epochMillis / 1000)
	  *
	  * Notice that, currently, it stores using [[Int]] (also the implementation
	  * method of Telegram), which will only store times before 2038-01-19 03:14:07.
	  */
	type EpochSeconds = Int
	
	/** The UNIX Epoch Time in day.
	  *
	  * aka. days since 00:00:00 UTC on Thursday, 1 January 1970.
	  *
	  * Normally is the epochDays = (epochMillis / 1000 / 60 / 60 / 24)
	  *
	  * Notice that, currently, it stores using [[Short]] (also the implementation
	  * method of Telegram), which will only store times before 2059-09-18.
	  */
	type EpochDays = Short
	object EpochDays:
		def fromEpochMillis (epochMillis: EpochMillis): EpochDays =
			(epochMillis / (1000*60*60*24)).toShort
	
	/** Time duration/interval in milliseconds. */
	type DurationMillis = Long
	
}
