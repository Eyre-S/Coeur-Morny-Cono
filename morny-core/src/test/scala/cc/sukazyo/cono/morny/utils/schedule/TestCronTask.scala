package cc.sukazyo.cono.morny.utils.schedule

import cc.sukazyo.cono.morny.util.schedule.{CronTask, Scheduler}
import cc.sukazyo.cono.morny.util.CommonFormat.formatDate
import cc.sukazyo.cono.morny.MornyCoreTests
import com.cronutils.builder.CronBuilder
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.field.expression.FieldExpressionFactory as C
import com.cronutils.model.time.ExecutionTime
import org.scalatest.tagobjects.Slow

import java.lang.System.currentTimeMillis
import java.time.{ZonedDateTime, ZoneOffset}
import java.time.temporal.ChronoUnit

class TestCronTask extends MornyCoreTests {
	
	"cron task works fine" taggedAs Slow in {
		
		val scheduler = Scheduler()
		val cronSecondly =
			CronBuilder.cron(
				CronDefinitionBuilder.defineCron
					.withSeconds.and
					.instance
			).withSecond(C.every(1)).instance
		Thread.sleep(
			ExecutionTime.forCron(cronSecondly)
				.timeToNextExecution(ZonedDateTime.now)
				.get.get(ChronoUnit.NANOS)/1000
		) // aligned current time to millisecond 000
		note(s"CronTask test time aligned to ${formatDate(currentTimeMillis, 0)}")
		
		var times = 0
		val task = CronTask("cron-task", cronSecondly, ZoneOffset.ofHours(0).normalized, {
			times = times + 1
			note(s"CronTask executed at ${formatDate(currentTimeMillis, 0)}")
		})
		scheduler ++ task
		Thread.sleep(10300)
		
		// it should be at 300ms position to 10 seconds
		
		scheduler % task
		scheduler.stop()
		note(s"CronTasks done at ${formatDate(currentTimeMillis, 0)}")
		times shouldEqual 10
		
	}
	
}
