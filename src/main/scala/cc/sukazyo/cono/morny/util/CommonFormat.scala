package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.util.EpochDateTime.{DurationMillis, EpochMillis}

import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}
import java.time.format.DateTimeFormatter

/** Some formatting (convert some data to some standard output type)
  * methods normalized based on Morny's usage
  *
  * contains:
  *   - [[DATE_TIME_PATTERN_FULL_MILLIS]] the standard date-time-millis [[String]] pattern format
  *   - [[formatDate]] convert UTC time millis (and hour-offset time zone)
  *                    to normalized date-time-millis [[String]]
  *   - [[formatDuration]] convert millis duration to normalized duration [[String]]
  *
  */
object CommonFormat {
	
	/** the standard date-time-millis [[String]] pattern format that Morny in use.
	  *
	  * pattern string is pattern of [[DateTimeFormatter]].
	  */
	//noinspection ScalaWeakerAccess
	val DATE_TIME_PATTERN_FULL_MILLIS = "yyyy-MM-dd HH:mm:ss:SSS"
	
	/** the formatted date-time-millis [[String]].
	  *
	  * time is formatted by pattern [[DATE_TIME_PATTERN_FULL_MILLIS]].
	  *
	  * @param timestamp millis timestamp. timestamp should be UTC alignment.
	  *
	  * @param utcOffset the hour offset of the time zone, the time-zone controls
	  *                  which local time describe will use.
	  *
	  *                  for example, timestamp `0` describes 1970-1-1 00:00:00 in
	  *                  UTC+0, so, use the `timestamp` `0` and `utfOffset` `0` will
	  *                  returns `"1970-1-1 00:00:00:000"`; however, at the same time,
	  *                  in UTC+8, the local time is 1970-1-1 08:00:00:000, so use
	  *                  the `timestamp` `0` and the `utcOffset` `8` will returns
	  *                  `"1970-1-1 08:00:00:000"`
	  *
	  * @return the time-zone local date-time-millis [[String]] describes the timestamp.
	  */
	def formatDate (timestamp: EpochMillis, utcOffset: Int): String =
		formatDate(timestamp, ZoneOffset.ofHours(utcOffset))
	
	/** the formatted date-time-millis [[String]].
	  *
	  * time is formatted by pattern [[DATE_TIME_PATTERN_FULL_MILLIS]].
	  *
	  * @param timestamp millis timestamp. timestamp should be UTC alignment.
	  *
	  * @param tz the time-zone controls which local time describe will use.
	  *
	  * @return the time-zone local date-time-millis [[String]] describes the timestamp.
	  */
	def formatDate (timestamp: EpochMillis, tz: ZoneOffset): String =
		DateTimeFormatter.ofPattern(DATE_TIME_PATTERN_FULL_MILLIS).format(
			LocalDateTime.ofInstant(
				Instant.ofEpochMilli(timestamp),
				ZoneId.ofOffset("UTC", tz)
			)
		)
	
	/** human readable [[String]] that describes the millis duration.
	  *
	  * @example {{{
	  *	    scala> formatDuration(10)
	  *	    val res0: String = 10ms
	  *
	  *	    scala> formatDuration(3000001)
	  *	    val res1: String = 50min 0s 1ms
	  *
	  *	    scala> formatDuration(94179047901720L)
	  *	    val res2: String = 1090035d 6h 38min 21s 720ms
	  * }}}
	  *
	  * @param duration time duration, in milliseconds
	  * @return time duration, human readable
	  */
	def formatDuration (duration: DurationMillis): String =
		val sb = new StringBuilder()
		if (duration > 1000 * 60 * 60 * 24) sb ++= (duration / (1000 * 60 * 60 * 24)).toString ++= "d "
		if (duration > 1000 * 60 * 60) sb ++= (duration / (1000 * 60 * 60) % 24).toString ++= "h "
		if (duration > 1000 * 60) sb ++= (duration / (1000 * 60) % 60).toString ++= "min "
		if (duration > 1000) sb ++= (duration / 1000 % 60).toString ++= "s "
		sb ++= (duration % 1000).toString ++= "ms"
		sb toString
	
}
