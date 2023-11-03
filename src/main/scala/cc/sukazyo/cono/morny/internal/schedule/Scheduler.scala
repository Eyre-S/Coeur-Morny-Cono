package cc.sukazyo.cono.morny.internal.schedule

import scala.collection.mutable

class Scheduler {
	
//	val taskList: util.TreeSet[Task] =
//		Collections.synchronizedSortedSet(util.TreeSet[Task]())
	private val taskList: mutable.TreeSet[Task] = mutable.TreeSet.empty
	private var exitAtNextRoutine = false
	private var waitForDone = false
	private var currentRunning: Task|Null = _
	private var currentRunning_isScheduledCancel = false
	private val runtime: Thread = new Thread {
		
		override def run (): Unit = {
			def willExit: Boolean =
				if exitAtNextRoutine then true
				else if waitForDone then
					taskList.synchronized:
						if taskList.isEmpty then true
						else false
				else false
			while (!willExit) {
				
				val nextMove: Task|Long = taskList.synchronized {
					taskList.headOption match
						case Some(_readyToRun) if System.currentTimeMillis >= _readyToRun.scheduledTimeMillis =>
							taskList -= _readyToRun
							currentRunning = _readyToRun
							_readyToRun
						case Some(_notReady) =>
							_notReady.scheduledTimeMillis - System.currentTimeMillis
						case None =>
							Long.MaxValue
				}
				
				nextMove match
					case readyToRun: Task =>
						
						this setName readyToRun.name
						
						try {
							readyToRun.main
						} catch case _: (Exception | Error) => {}
						
						this setName s"${readyToRun.name}#post"
						
						currentRunning match
							case routine: RoutineTask =>
								routine.nextRoutineTimeMillis(routine.currentScheduledTimeMillis) match
									case next: Long =>
										routine.currentScheduledTimeMillis = next
										if (!currentRunning_isScheduledCancel) schedule(routine)
									case _ =>
							case _ =>
						
						this setName runnerName
						currentRunning = null
						
					case needToWaitMillis: Long =>
						try Thread.sleep(needToWaitMillis)
						catch case _: InterruptedException => {}
				
			}
		}
		
	}
	runtime.start()
	
	//noinspection ScalaWeakerAccess
	def runnerName: String =
		this.toString
	
	def ++ (task: Task): this.type =
		schedule(task)
		this
	def schedule (task: Task): Boolean =
		try taskList.synchronized:
			taskList add task
		finally runtime.interrupt()
	
	def % (task: Task): this.type =
		cancel(task)
		this
	def cancel (task: Task): Boolean =
		try {
			val succeed = taskList.synchronized { taskList remove task }
			if succeed then succeed
			else if task == currentRunning then
				currentRunning_isScheduledCancel = true
				true
			else false
		}
		finally runtime.interrupt()
	
	def amount: Int =
		taskList.size
	
	def state: Thread.State =
		runtime.getState
	
	def stop (): Unit =
		exitAtNextRoutine = true
		runtime.interrupt()
	
	def waitForStop (): Unit =
		stop()
		runtime.join()
	
	//noinspection ScalaWeakerAccess
	def tagStopAtAllDone (): Unit =
		waitForDone = true
	
	def waitForStopAtAllDone(): Unit =
		tagStopAtAllDone()
		runtime.join()
	
}
