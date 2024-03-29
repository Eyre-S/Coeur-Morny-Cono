package cc.sukazyo.cono.morny.test.utils.schedule

import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.util.schedule.{DelayedTask, Scheduler, Task}
import org.scalatest.tagobjects.Slow

import scala.collection.mutable

class SchedulerTest extends MornyTests {
	
	"While executing tasks using scheduler :" - {
		
		"Task with scheduleTime smaller than current time should be executed immediately" in {
			val scheduler = Scheduler()
			val time = System.currentTimeMillis
			var doneTime: Option[Long] = None
			scheduler ++ Task("task", 0, {
				doneTime = Some(System.currentTimeMillis)
			})
			Thread.sleep(10)
			scheduler.stop()
			doneTime shouldBe defined
			info(s"Immediately Task done in ${doneTime.get - time}ms")
		}
		
		"Task's running thread name should be task name" in {
			val scheduler = Scheduler()
			var executedThread: Option[String] = None
			scheduler ++ Task("task", 0, {
				executedThread = Some(Thread.currentThread.getName)
			})
			scheduler.waitForStopAtAllDone()
			executedThread shouldEqual Some("task")
		}
		
		"Task's execution order should be ordered by task Ordering but not insert order" taggedAs Slow in {
			val scheduler = Scheduler()
			val result = mutable.ArrayBuffer.empty[String]
			scheduler
				++ DelayedTask("task-later", 400L, { result += Thread.currentThread.getName })
				++ DelayedTask("task-very-late", 800L, { result += Thread.currentThread.getName })
				++ DelayedTask("task-early", 100L, { result += Thread.currentThread.getName })
			scheduler.waitForStopAtAllDone()
			result.toArray shouldEqual Array("task-early", "task-later", "task-very-late")
		}
		
	}
	
}
