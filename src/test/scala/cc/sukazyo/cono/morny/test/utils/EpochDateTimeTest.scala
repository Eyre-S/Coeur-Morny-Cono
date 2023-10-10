package cc.sukazyo.cono.morny.test.utils

import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis
import org.scalatest.prop.TableDrivenPropertyChecks

class EpochDateTimeTest extends MornyTests with TableDrivenPropertyChecks {
	
	"while converting date-time string to time-millis : " - {
		
		"while using ISO-Offset-Date-Time : " - {
			
			val examples = Table(
				("str"                    , "zone"  , "millis"),
				("2011-12-03T10:15:30"    , "+01:00", 1322903730000L),
				("2023-10-10T06:12:44.857", "Z"     , 1696918364857L),
				("2023-10-10T02:12:44.857", "-04:00", 1696918364857L),
				("2023-10-10T14:12:44.857", "+8"    , 1696918364857L),
				("1938-04-24T22:13:20"    , "Z"     ,-1000000000000L),
			)
			
			forAll(examples) { (str, zone, millis) =>
				s"$str should be epoch time millis $millis" in:
					EpochMillis(str, zone) shouldEqual millis
			}
			
		}
		
	}
	
}
