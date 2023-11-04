package cc.sukazyo.cono.morny.daemon

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.daemon.MedicationTimer.calcNextRoutineTimestamp
import cc.sukazyo.cono.morny.util.schedule.RoutineTask
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.util.CommonFormat
import com.pengrad.telegrambot.model.{Message, MessageEntity}
import com.pengrad.telegrambot.request.{EditMessageText, SendMessage}
import com.pengrad.telegrambot.response.SendResponse

import java.time.{LocalDateTime, ZoneOffset}
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions

class MedicationTimer (using coeur: MornyCoeur) {
	
	private val NOTIFY_MESSAGE = "🍥⏲"
	private val DAEMON_THREAD_NAME_DEF = "MedicationTimer"
	
	private val use_timeZone = coeur.config.medicationTimerUseTimezone
	import scala.jdk.CollectionConverters.SetHasAsScala
	private val notify_atHour: Set[Int] = coeur.config.medicationNotifyAt.asScala.toSet.map(_.intValue)
	private val notify_toChat = coeur.config.medicationNotifyToChat
	
	private var lastNotify_messageId: Option[Int] = None
	
	private val scheduleTask: RoutineTask = new RoutineTask {
		
		override def name: String = DAEMON_THREAD_NAME_DEF
		
		def calcNextSendTime: Long =
			val next_time = calcNextRoutineTimestamp(System.currentTimeMillis, use_timeZone, notify_atHour)
			logger info s"medication timer will send next notify at ${CommonFormat.formatDate(next_time, use_timeZone.getTotalSeconds / 60 / 60)} with $use_timeZone [$next_time]"
			next_time
		
		override def firstRoutineTimeMillis: Long =
			calcNextSendTime
		
		override def nextRoutineTimeMillis (previousRoutineScheduledTimeMillis: Long): Long | Null =
			calcNextSendTime
		
		override def main: Unit = {
			sendNotification()
			logger info "medication notify sent."
		}
		
	}
	
	def start(): Unit =
		if ((notify_toChat == -1) || (notify_atHour isEmpty))
			logger notice "Medication Timer disabled : related param is not complete set"
			return;
		coeur.tasks ++ scheduleTask
		logger notice "Medication Timer started."
	
	def stop(): Unit =
		coeur.tasks % scheduleTask
		logger notice "Medication Timer stopped."
	
	private def sendNotification(): Unit = {
		val sendResponse: SendResponse = coeur.account exec SendMessage(notify_toChat, NOTIFY_MESSAGE)
		if sendResponse isOk then lastNotify_messageId = Some(sendResponse.message.messageId)
		else lastNotify_messageId = None
	}
	
	def refreshNotificationWrite (edited: Message): Unit = {
		if (lastNotify_messageId isEmpty) || (lastNotify_messageId.get != (edited.messageId toInt)) then return
		import cc.sukazyo.cono.morny.util.CommonFormat.formatDate
		val editTime = formatDate(edited.editDate*1000, use_timeZone.getTotalSeconds/60/60)
		val entities = ArrayBuffer.empty[MessageEntity]
		if edited.entities ne null then entities ++= edited.entities
		entities += MessageEntity(MessageEntity.Type.italic, edited.text.length + "\n-- ".length, editTime.length)
		coeur.account exec EditMessageText(
			notify_toChat,
			edited.messageId,
			edited.text + s"\n-- $editTime --"
		).entities(entities toArray:_*)
		lastNotify_messageId = None
	}
	
}

object MedicationTimer {
	
	@throws[IllegalArgumentException]
	def calcNextRoutineTimestamp (baseTimeMillis: Long, zone: ZoneOffset, notifyAt: Set[Int]): Long = {
		if (notifyAt isEmpty) throw new IllegalArgumentException("notify time is not set")
		var time = LocalDateTime.ofEpochSecond(
			baseTimeMillis / 1000, ((baseTimeMillis % 1000) * 1000 * 1000) toInt,
			zone
		).withMinute(0).withSecond(0).withNano(0)
		time = time plusHours 1
		while (!(notifyAt contains(time getHour))) {
			time = time plusHours 1
		}
		(time toInstant zone) toEpochMilli
	}
	
}
