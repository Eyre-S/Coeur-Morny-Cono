package cc.sukazyo.cono.morny.util.schedule

import cc.sukazyo.cono.morny.system.utils.EpochDateTime.EpochMillis
import com.cronutils.model.time.ExecutionTime
import com.cronutils.model.Cron

import java.time.{Instant, ZonedDateTime, ZoneId}
import scala.jdk.OptionConverters.*

trait CronTask extends RoutineTask {
	
	private lazy val cronCalc = ExecutionTime.forCron(cron)
	
	def cron: Cron
	
	def zone: ZoneId
	
	override def firstRoutineTimeMillis: EpochMillis =
		cronCalc.nextExecution(
			ZonedDateTime.ofInstant(
				Instant.now, zone
			)
		).get.toInstant.toEpochMilli
	
	override def nextRoutineTimeMillis (previousRoutineScheduledTimeMillis: EpochMillis): EpochMillis | Null =
		cronCalc.nextExecution(
			ZonedDateTime.ofInstant(
				Instant.ofEpochMilli(previousRoutineScheduledTimeMillis),
				zone
			)
		).toScala match
			case Some(time) => time.toInstant.toEpochMilli
			case None => null
	
}

object CronTask {
	
	def apply (_name: String, _cron: Cron, _zone: ZoneId, _main: =>Unit): CronTask =
		new CronTask:
			override def name: String = _name
			override def cron: Cron = _cron
			override def zone: ZoneId = _zone
			override def main: Unit = _main
	
}
