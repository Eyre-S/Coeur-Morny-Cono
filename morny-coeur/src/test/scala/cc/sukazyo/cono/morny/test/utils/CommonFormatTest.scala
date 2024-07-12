package cc.sukazyo.cono.morny.test.utils

import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.util.CommonFormat.{formatDate, formatDuration}
import org.scalatest.prop.TableDrivenPropertyChecks

class CommonFormatTest extends MornyTests with TableDrivenPropertyChecks {
	
	"while using #formatDate :" - {
		
		val examples = Table(
			("time_text"              , "timestamp", "zone_offset"),
			("2022-10-02 01:54:30:402", 1664646870402L, 8),
			("1970-01-01 08:00:00:001", 1L, 8),
			("1969-12-31 23:00:00:000", 0L, -1),
		)
		
		forAll(examples) { (time_text, timestamp, zone_offset) =>
			s"time $time_text in TimeZone($zone_offset) should be UTC timestamp $timestamp" in:
				formatDate(timestamp, zone_offset) shouldEqual time_text
		}
		
	}
	
	"while using #formatDuration :" - {
		
		val examples = Table(
			("time_millis", "duration_text"),
			(100L         , "100ms"),
			(3000L        , "3s 0ms"),
			(326117522L   , "3d 18h 35min 17s 522ms"),
			(53373805L    , "14h 49min 33s 805ms"),
			(3600001L     , "1h 0min 0s 1ms")
		)
		
		forAll(examples) { (time_millis, duration_text) =>
			
			s"duration ($time_millis) millis should be formatted to '$duration_text'" in:
				formatDuration(time_millis) shouldEqual duration_text
				0 should equal (0)
			
		}
		
	}
	
}
