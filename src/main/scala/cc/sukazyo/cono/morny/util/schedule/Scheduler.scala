package cc.sukazyo.cono.morny.util.schedule

import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis

import scala.annotation.targetName
import scala.collection.mutable

/** Stores some [[Task tasks]] and execute them at time defined in task.
  *
  * == Usage ==
  *
  * Start a new scheduler instance by create a new Scheduler object, and
  * the scheduler runner will automatic start to run.
  *
  * Using [[Scheduler.++]] or [[Scheduler.schedule]] to add a [[Task]] to
  * a Scheduler instance.
  *
  * If you want to remove a task, use [[Scheduler.%]] or [[Scheduler.cancel]].
  * Removal task should be the same task object, but not just the same name.
  *
  * The scheduler will not automatic stop when the tasks is all done and the
  * main thread is stopped. You can/should use [[stop]], [[waitForStop]],
  * [[tagStopAtAllDone]], [[waitForStopAtAllDone]] to async or syncing stop
  * the scheduler.
  *
  * == Implementation details ==
  *
  * Inside the Scheduler, the runner's implementation is very similar to
  * java's [[java.util.Timer]]: There's a task queue sorted by [[Task.scheduledTimeMillis]]
  * (which is the default order method implemented in [[Task]]), and a
  * runner getting the most previous task in the queue, and sleep to that
  * task's execution time.
  *
  * Every time the runner is executing a task, it will firstly set its thread name
  * to [[Task.name]]. After running a task, if the task have some post-process
  * method (like [[RoutineTask]] will do prepare for next routine), the runner's
  * thread name will be set to <code>[[Task.name]]#post</code>. After all of
  * that, the task is fully complete, and the runner's thread name will be
  * reset to [[runnerName]].
  */
class Scheduler {
	
	/** Status tag of this scheduler. */
	//noinspection ScalaWeakerAccess
	enum State:
		/** The scheduler is on init stage, have not prepared for running tasks. */
		case INIT
		/** The scheduler is managing the task queue, processing the exit signal,
		  * and looking for the next running task. */
		case PREPARE_RUN
		/** The scheduler is infinitely waiting due to there's nothing in the task
		  * queue. */
		case WAITING_EMPTY
		/** The scheduler is waiting until the next task's running time. */
		case WAITING
		/** The scheduler is running a task in the runner. */
		case RUNNING
		/** The scheduler is executing a task's post effect. */
		case RUNNING_POST
		/** The scheduler have been stopped, will not process any more tasks. */
		case END
	
	private val taskList: mutable.TreeSet[Task] = mutable.TreeSet.empty
	private var exitAtNextRoutine = false
	private var waitForDone = false
//	private var currentRunning: Task|Null = _
	private var runtimeStatus = State.INIT
	private val runtime: Thread = new Thread {
		
		override def run (): Unit = {
			def willExit: Boolean =
				if exitAtNextRoutine then true
				else if waitForDone then
					taskList.synchronized:
						if taskList.isEmpty then true
						else false
				else false
			taskList.synchronized { while (!willExit) {
				
				runtimeStatus = State.PREPARE_RUN
				
				val nextMove: Task|EpochMillis|"None" =
					taskList.headOption match
						case Some(_readyToRun) if System.currentTimeMillis >= _readyToRun.scheduledTimeMillis =>
							taskList -= _readyToRun
//							currentRunning = _readyToRun
							_readyToRun
						case Some(_notReady) =>
							_notReady.scheduledTimeMillis - System.currentTimeMillis
						case None => "None"
				
				nextMove match
					case readyToRun: Task =>
						
						runtimeStatus = State.RUNNING
						this setName readyToRun.name
						
						try {
							readyToRun.main
						} catch case _: (Exception | Error) => {}
						
						runtimeStatus = State.RUNNING_POST
						this setName s"${readyToRun.name}#post"
						
						// this if is used for check if post effect need to be
						//  run. It is useless since the wait/notify changes.
						if false then {}
						else {
							readyToRun match
								case routine: RoutineTask =>
									routine.nextRoutineTimeMillis(routine.currentScheduledTimeMillis.get) match
										case next: EpochMillis =>
											routine.currentScheduledTimeMillis = Some(next)
											schedule(routine)
										case _ =>
								case _ =>
						}
						
//						currentRunning = null
						this setName runnerName
						
					case needToWaitMillis: EpochMillis =>
						runtimeStatus = State.WAITING
						try taskList.wait(needToWaitMillis)
						catch case _: (InterruptedException|IllegalArgumentException) => {}
					case _: "None" =>
						runtimeStatus = State.WAITING_EMPTY
						try taskList.wait()
						catch case _: InterruptedException => {}
				
			}}
			runtimeStatus = State.END
		}
		
	}
	runtime setName runnerName
	runtime.start()
	
	/** Name of the scheduler runner.
	  * Currently, same with the scheduler [[toString]]
	  */
	//noinspection ScalaWeakerAccess
	def runnerName: String =
		this.toString
	
	/** Add one task to scheduler task queue.
	  * @return this scheduler for chained call.
	  */
	@targetName("scheduleIt")
	def ++ (task: Task): this.type =
		schedule(task)
		this
	/** Add one task to scheduler task queue.
	  * @return [[true]] if the task is added.
	  */
	def schedule (task: Task): Boolean =
		taskList.synchronized:
			try taskList add task
			finally taskList.notifyAll()
	
	/** Remove the task from scheduler task queue.
	  *
	  * If the removal task is running, the current run will be done, but will
	  * not do the post effect of the task (like schedule the next routine
	  * of [[RoutineTask]]).
	  *
	  * @return this scheduler for chained call.
	  */
	@targetName("cancelIt")
	def % (task: Task): this.type =
		cancel(task)
		this
	/** Remove the task from scheduler task queue.
	  *
	  * If the removal task is running, the method will wait for the current run
	  * complete (and current run post effect complete), then do remove.
	  *
	  * @return [[true]] if the task is in task queue or is running, and have been
	  *         succeed removed from task queue.
	  */
	def cancel (task: Task): Boolean =
		taskList synchronized:
			try taskList remove task
			finally taskList.notifyAll()
	
	/** Count of tasks in the task queue.
	  *
	  * Do not contains the running task.
	  */
	def amount: Int =
		taskList.size
	
	/** Current [[State status]] */
	def state: this.State =
		runtimeStatus
	
	/** This scheduler's runner thread state */
	def runnerState: Thread.State =
		runtime.getState
	
	/** Manually update the task scheduler.
	  * 
	  * If the inner state of the scheduler somehow changed and cannot automatically
	  * update schedule states to schedule the new state, you can call this method
	  * to manually let the task scheduler reschedule it.
	  * 
	  * You can also use it with some tick-guard like [[cc.sukazyo.cono.morny.util.time.WatchDog]]
	  * to make the scheduler avoid fails when machine fall asleep or some else conditions.
	  */
	def notifyIt(): Unit =
		taskList synchronized:
			taskList.notifyAll()
	
	/** Stop the scheduler's runner, no matter how much task is not run yet.
	  * 
	  * After call this, it will immediately give a signal to the runner for
	  * stopping it. If the runner is not running any task, it will stop immediately;
	  * If there's one task running, the runner will continue executing until
	  * the current task is done and the current task's post effect is done, then
	  * stop.
	  * 
	  * This method is async, means complete this method does not means the
	  * runner is stopped. If you want a sync version, see [[waitForStop]].
	  */
	def stop (): Unit =
		taskList synchronized:
			exitAtNextRoutine = true
			taskList.notifyAll()
	
	/** Stop the scheduler's runner, no matter how much task is not run yet,
	  * and wait for the runner stopped.
	  * 
	  * It do the same job with [[stop]], the only different is this method
	  * will join the runner thread to wait it stopped.
	  * 
	  * @throws InterruptedException if any thread has interrupted the current
	  *                              thread. The interrupted status of the current
	  *                              thread is cleared when this exception is thrown.
	  */
	@throws[InterruptedException]
	def waitForStop (): Unit =
		stop()
		runtime.join()
	
	/** Tag this scheduler runner stop when all of the scheduler's task in task
	  * queue have been stopped.
	  * 
	  * After called this method, the runner will exit when all tasks executed done
	  * and there's no more task can be found in task queue.
	  * 
	  * Notice that if there's [[RoutineTask]] in task queue, due to the routine
	  * task will re-enter the task queue in task's post effect stage after executed,
	  * it will cause the task queue will never be empty. You may need to remove all
	  * routine tasks before calling this.
	  *
	  * This method is async, means complete this method does not means the
	  * runner is stopped. If you want a sync version, see [[waitForStopAtAllDone]].
	  */
	//noinspection ScalaWeakerAccess
	def tagStopAtAllDone (): Unit =
		taskList synchronized:
			waitForDone = true
			taskList.notifyAll()
	
	/** Tag this scheduler runner stop when all of the scheduler's task in task
	  * queue have been stopped, and wait for the runner stopped.
	  *
	  * It do the same job with [[tagStopAtAllDone]], the only different is this method
	  * will join the runner thread to wait it stopped.
	  *
	  * @throws InterruptedException if any thread has interrupted the current
	  *                              thread. The interrupted status of the current
	  *                              thread is cleared when this exception is thrown.
	  */
	@throws[InterruptedException]
	def waitForStopAtAllDone(): Unit =
		tagStopAtAllDone()
		runtime.join()
	
}
