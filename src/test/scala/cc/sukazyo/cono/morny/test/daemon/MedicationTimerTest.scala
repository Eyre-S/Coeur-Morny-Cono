package cc.sukazyo.cono.morny.test.daemon

import cc.sukazyo.cono.morny.medication_timer.MedicationTimer
import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis
import org.scalatest.prop.TableDrivenPropertyChecks

import java.time.ZoneOffset

class MedicationTimerTest extends MornyTests with TableDrivenPropertyChecks {
	
	"on calculating next notify time :" - {
		
		val examples = Table(
			("current", "notifyAt", "useTimezone", "nextNotifyTime"),
			(("2023-10-09T23:54:10.000", "+8"), Set(1, 5, 19), "+8", ("2023-10-10T01:00:00.000", "+8")),
			(("2022-11-13T13:14:15.000", "+2"), Set(7, 19, 21), "+2", ("2022-11-13T19:00:00.000", "+2")),
			(("2022-11-13T13:14:35.000", "+8"), Set(7, 19, 21), "+8", ("2022-11-13T19:00:00", "+8")),
			(("2022-11-13T13:14:35.174", "+2"), Set(7, 19, 21), "+2", ("2022-11-13T19:00:00", "+2")),
			(("1998-02-01T08:14:35.871", "+8"), Set(7, 19, 21), "+8", ("1998-02-01T19:00:00", "+8")),
			(("2022-11-13T00:00:00.000", "-1"), Set(7, 19, 21), "-1", ("2022-11-13T07:00:00", "-1")),
			(("2022-11-21T19:00:00.000", "+0"), Set(7, 19, 21), "+0", ("2022-11-21T21:00:00", "+0")),
			(("2022-12-31T21:00:00.000", "+0"), Set(7, 19, 21), "+0", ("2023-01-01T07:00:00", "+0")),
			(("2125-11-18T23:45:27.062", "+0"), Set(7, 19, 21), "+0", ("2125-11-19T07:00:00", "+0"))
		)
		
		forAll(examples) { (current, notifyAt, useTimezone, nextNotifyTime) =>
			val _curr = EpochMillis(current)
			val _tz = ZoneOffset of useTimezone
			val _next = EpochMillis(nextNotifyTime)
			
			s"at time [$_curr], and need to be notify at hours ${notifyAt.mkString(",")} with $_tz :" - {
				s"next notify should at time [$_curr]" in {
					MedicationTimer.calcNextRoutineTimestamp(_curr, _tz, notifyAt) shouldEqual _next
				}
			}
			
		}
		
	}
	
}
