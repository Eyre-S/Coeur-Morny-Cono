package cc.sukazyo.cono.morny.test.utils

import cc.sukazyo.cono.morny.system.utils.EpochDateTime.{EpochDays, EpochMillis, EpochSeconds}
import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks

class EpochDateTimeTest extends MornyTests with TableDrivenPropertyChecks {
	
	"while converting to EpochMillis :" - {
		
		"from EpochSeconds :" - {
			
			val examples = Table[EpochSeconds, EpochMillis](
				("EpochSeconds", "EpochMillis"),
				(1699176068, 1699176068000L),
				(1699176000, 1699176000000L),
				(1, 1000L),
			)
			
			forAll(examples) { (epochSeconds, epochMillis) =>
				s"EpochSeconds($epochSeconds) should be converted to EpochMillis($epochMillis)" in {
					(EpochMillis fromSeconds epochSeconds) shouldEqual epochMillis
				}
			}
			
		}
		
	}
	
	"while converting to EpochDays :" - {
		
		"from EpochMillis :" - {
			
			val examples = Table(
				("EpochMillis", "EpochDays"),
				(0L, 0),
				(1000L, 0),
				(80000000L, 0),
				(90000000L, 1),
				(1699176549059L, 19666)
			)
			
			forAll(examples) { (epochMillis, epochDays) =>
				s"EpochMillis($epochMillis) should be converted to EpochDays($epochDays)" in {
					(EpochDays fromMillis epochMillis) shouldEqual epochDays
				}
			}
			
		}
		
	}
	
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
