package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.util.EpochDateTime.{DurationMillis, EpochMillis}

import java.time.{Duration, Instant, LocalDateTime, ZoneId, ZoneOffset}
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

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
	  *                  for example, timestamp [[0]] describes 1970-1-1 00:00:00 in
	  *                  UTC+0, so, use the `timestamp` `0` and `utfOffset` `0` will
	  *                  return `"1970-1-1 00:00:00:000"`; however, at the same time,
	  *                  in UTC+8, the local time is 1970-1-1 08:00:00:000, so use
	  *                  the `timestamp` `0` and the `utcOffset` `8` will return
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
	
	/** human-readable [[String]] that describes the millis duration.
	  *
	  * {{{
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
	
	def formatDuration (duration: Duration, minUnit: ChronoUnit = ChronoUnit.MILLIS): String =
		List[(Long, String, ChronoUnit)](
			(duration.toNanosPart % 1_000_000, "ns",  ChronoUnit.NANOS),
			(duration.toMillisPart,            "ms",  ChronoUnit.MILLIS),
			(duration.toSecondsPart,           "s",   ChronoUnit.SECONDS),
			(duration.toMinutesPart,           "min", ChronoUnit.MINUTES),
			(duration.toHoursPart,             "h",   ChronoUnit.HOURS),
			(duration.toDaysPart,              "d",   ChronoUnit.DAYS)
		).filter((_, _, level) => level.ordinal() >= minUnit.ordinal())
			.map((value, unit, _) => (value, unit))
			.map((value, unit) => if value != 0 then Right(s"$value$unit") else Left(s"$value$unit"))
			.reverse.dropWhile(_.isLeft)
			.map {
				case Left(value) => value
				case Right(value) => value
			}.mkString(" ")
	
	def formatDurationTimers (duration: Duration, minUnit: ChronoUnit = ChronoUnit.SECONDS): String =
		val result = List[(Long, Int, String, ChronoUnit)]( // define each levels parameter
			(duration.toNanosPart % 1_000,        3, "",  ChronoUnit.NANOS),
			(duration.toNanosPart / 1000 % 1_000, 3, ".", ChronoUnit.MICROS),
			(duration.toMillisPart,               3, ".", ChronoUnit.MILLIS),
			(duration.toSecondsPart,              2, ":", ChronoUnit.SECONDS),
			(duration.toMinutesPart,              2, ":", ChronoUnit.MINUTES),
			(duration.toHoursPart,                2, ":", ChronoUnit.HOURS),
			(duration.toDaysPart,                 0, "!", ChronoUnit.DAYS)
		).filter((_, _, _, level) => level.ordinal() >= minUnit.ordinal()) // filter out smaller than minUnit levels
			.map { (value, minWidth, prefixed, level) => // stringify the duration number, and fill to left if it is 0
				val stringed = value.toString.reverse.padTo(minWidth, '0').reverse
				val data = (prefixed, stringed)
				if value != 0 then (Right(data), level) else (Left(data), level)
			}.reverse.dropWhile{ (value, level) => // filter out units with 0 value, on larger units side
				value.isLeft && (level.ordinal() > ChronoUnit.SECONDS.ordinal()) // but do not filter out units smaller than seconds
			}.map((value, _) => value).map { // just fold data
				case Left(data) => data
				case Right(data) => data
			}.map((prefixed, value) => prefixed + value)
			.mkString("") // make it as a string
			.drop(1) // drop unnecessary prefixed unit-prefix
		if result.startsWith("00.") then // when the max unit is already 0 second, return with 0 seconds
			result.drop(1)
		else if result.contains(":") || result.contains(".") then // when there contains more than one unit
			result.dropWhile(_=='0')                              // drop the prefixed 0 on the largest unit
		else "0:" + result // when the max unit is smaller than seconds, add the 0-minute parts
	
}
