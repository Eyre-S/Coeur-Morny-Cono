package cc.sukazyo.cono.morny.medication_timer

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.medication_timer.MedicationTimer.calcNextRoutineTimestamp
import cc.sukazyo.cono.morny.util.schedule.RoutineTask
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.util.CommonFormat
import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis
import com.cronutils.builder.CronBuilder
import com.cronutils.model.definition.{CronDefinition, CronDefinitionBuilder}
import com.cronutils.model.time.ExecutionTime
import com.pengrad.telegrambot.model.{Message, MessageEntity}
import com.pengrad.telegrambot.request.{EditMessageText, SendMessage}
import com.pengrad.telegrambot.response.SendResponse
import com.pengrad.telegrambot.TelegramBot

import java.time.{Instant, ZonedDateTime, ZoneOffset}
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions

class MedicationTimer (using coeur: MornyCoeur) {
	private given TelegramBot = coeur.account
	
	private val NOTIFY_MESSAGE = "🍥⏲"
	private val DAEMON_THREAD_NAME_DEF = "MedicationTimer"
	
	private val use_timeZone = coeur.config.medicationTimerUseTimezone
	import scala.jdk.CollectionConverters.SetHasAsScala
	private val notify_atHour: Set[Int] = coeur.config.medicationNotifyAt.asScala.toSet.map(_.intValue)
	private val notify_toChat = coeur.config.medicationNotifyToChat
	
	private var lastNotify_messageId: Option[Int] = None
	
	private val scheduleTask: RoutineTask = new RoutineTask {
		
		override def name: String = DAEMON_THREAD_NAME_DEF
		
		def calcNextSendTime: EpochMillis =
			val next_time = calcNextRoutineTimestamp(System.currentTimeMillis, use_timeZone, notify_atHour)
			logger `info` s"medication timer will send next notify at ${CommonFormat.formatDate(next_time, use_timeZone.getTotalSeconds / 60 / 60)} with $use_timeZone [$next_time]"
			next_time
		
		override def firstRoutineTimeMillis: EpochMillis =
			calcNextSendTime
		
		override def nextRoutineTimeMillis (previousRoutineScheduledTimeMillis: EpochMillis): EpochMillis | Null =
			calcNextSendTime
		
		override def main: Unit = {
			sendNotification()
			logger `info` "medication notify sent."
		}
		
	}
	
	def start(): Unit =
		if ((notify_toChat == -1) || (notify_atHour isEmpty))
			logger `notice` "Medication Timer disabled : related param is not complete set"
			return;
		coeur.tasks ++ scheduleTask
		logger `notice` "Medication Timer started."
	
	def stop(): Unit =
		coeur.tasks % scheduleTask
		logger `notice` "Medication Timer stopped."
	
	private def sendNotification(): Unit = {
		val sendResponse: SendResponse = SendMessage(notify_toChat, NOTIFY_MESSAGE).unsafeExecute
		if sendResponse isOk then lastNotify_messageId = Some(sendResponse.message.messageId)
		else lastNotify_messageId = None
	}
	
	def refreshNotificationWrite (edited: Message): Boolean = {
		if (lastNotify_messageId isEmpty) || (lastNotify_messageId.get != (edited.messageId toInt)) then return false
		import cc.sukazyo.cono.morny.util.CommonFormat.formatDate
		val editTime = formatDate(edited.editDate*1000, use_timeZone.getTotalSeconds/60/60)
		val entities = ArrayBuffer.empty[MessageEntity]
		if edited.entities ne null then entities ++= edited.entities
		entities += MessageEntity(MessageEntity.Type.italic, edited.text.length + "\n-- ".length, editTime.length)
		EditMessageText(
			notify_toChat,
			edited.messageId,
			edited.text + s"\n-- $editTime --"
		).entities((entities toArray)*).unsafeExecute
		lastNotify_messageId = None
		true
	}
	
}

object MedicationTimer {
	
	//noinspection ScalaWeakerAccess
	val cronDef: CronDefinition = CronDefinitionBuilder.defineCron
		.withHours.and
		.instance
	
	@throws[IllegalArgumentException]
	def calcNextRoutineTimestamp (baseTimeMillis: EpochMillis, zone: ZoneOffset, notifyAt: Set[Int]): EpochMillis = {
		if (notifyAt isEmpty) throw new IllegalArgumentException("notify time is not set")
		import com.cronutils.model.field.expression.FieldExpressionFactory.*
		ExecutionTime.forCron(CronBuilder.cron(cronDef)
			.withHour(and({
				import scala.jdk.CollectionConverters.*
				(for (i <- notifyAt) yield on(i)).toList.asJava
			}))
			.instance
		).nextExecution(
			ZonedDateTime `ofInstant` (Instant `ofEpochMilli` baseTimeMillis, zone.normalized)
		).get.toInstant.toEpochMilli
	}
	
}
