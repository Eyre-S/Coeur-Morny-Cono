package cc.sukazyo.cono.morny.test.utils.schedule

import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.util.schedule.Task
import org.scalatest.tagobjects.Slow

class TaskBasicTest extends MornyTests {
	
	"while comparing tasks :" - {
		
		"tasks with different scheduleTime should be compared using scheduledTime" in {
			Task("task-a", 21747013400912L, {}) should be > Task("task-b", 21747013400138L, {})
			Task("task-a", 100L, {}) should be > Task("task-b", 99L, {})
			Task("task-a", 100L, {}) should be < Task("task-b", 101, {})
			Task("task-a", -19943L, {}) should be < Task("task-b", 0L, {})
		}
		
		"task with the same scheduledTime should not be equal" in {
			Task("same-task?", 0L, {}) should not equal Task("same-task?", 0L, {})
		}
		
		"tasks which is only the same object should be equal" in {
			def createNewTask = Task("same-task?", 0L, {})
			val task1 = createNewTask
			val task2 = createNewTask
			val task1_copy = task1
			task1 shouldEqual task1_copy
			task1 should not equal task2
		}
		
	}
	
	"task can be sync executed by calling its main method." taggedAs Slow in {
		
		Thread.currentThread `setName` "parent-thread"
		val data = StringBuilder("")
		val task = Task("some-task", 0L, {
			Thread.sleep(100)
			data ++= Thread.currentThread.getName ++= " // " ++= "task-complete"
		})
		task.main
		data.toString shouldEqual "parent-thread // task-complete"
		
	}
	
}
