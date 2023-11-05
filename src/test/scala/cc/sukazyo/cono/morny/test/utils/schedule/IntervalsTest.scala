package cc.sukazyo.cono.morny.test.utils.schedule

import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.util.schedule.{IntervalWithTimesTask, Scheduler}
import org.scalatest.tagobjects.Slow

class IntervalsTest extends MornyTests {
	
	"IntervalWithTimesTest should work even scheduler is scheduled to stop" taggedAs Slow in {
		val scheduler = Scheduler()
		var times = 0
		scheduler ++ IntervalWithTimesTask("intervals-10", 200, 10, {
			times = times + 1
		})
		val startTime = System.currentTimeMillis()
		scheduler.waitForStopAtAllDone()
		val timeUsed = System.currentTimeMillis() - startTime
		times shouldEqual 10
		timeUsed should (be <= 2100L and be >= 1900L)
		info(s"interval 200ms for 10 times used time ${timeUsed}ms")
	}
	
}
