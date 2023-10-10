package cc.sukazyo.cono.morny.daemon

import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.daemon.MedicationTimer.calcNextRoutineTimestamp
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.{Message, MessageEntity}
import com.pengrad.telegrambot.request.{EditMessageText, SendMessage}
import com.pengrad.telegrambot.response.SendResponse

import java.time.{LocalDateTime, ZoneOffset}
import scala.collection.mutable.ArrayBuffer
import scala.language.implicitConversions

class MedicationTimer (using coeur: MornyCoeur) extends Thread {
	
	private val NOTIFY_MESSAGE = "🍥⏲"
	private val DAEMON_THREAD_NAME_DEF = "MedicationTimer"
	
	private val use_timeZone = coeur.config.medicationTimerUseTimezone
	import scala.jdk.CollectionConverters.SetHasAsScala
	private val notify_atHour: Set[Int] = coeur.config.medicationNotifyAt.asScala.toSet.map(_.intValue)
	private val notify_toChat = coeur.config.medicationNotifyToChat
	
	this.setName(DAEMON_THREAD_NAME_DEF)
	
	private var lastNotify_messageId: Option[Int] = None
	
	override def run (): Unit = {
		
		if ((notify_toChat == -1) || (notify_atHour isEmpty)) {
			logger info "Medication Timer disabled : related param is not complete set"
			return
		}
		
		logger info "Medication Timer started."
		while (!this.isInterrupted) {
			try {
				waitToNextRoutine()
				sendNotification()
			} catch
				case _: InterruptedException =>
					interrupt()
					logger info "MedicationTimer was interrupted, will be exit now"
				case ill: IllegalArgumentException =>
					logger warn "MedicationTimer will not work due to: " + ill.getMessage
					interrupt()
				case e =>
					logger error
							s"""unexpected error occurred on NotificationTimer
							   |${exceptionLog(e)}"""
							.stripMargin
					coeur.daemons.reporter.exception(e)
		}
		logger info "Medication Timer stopped."
		
	}
	
	private def sendNotification(): Unit = {
		val sendResponse: SendResponse = coeur.account exec SendMessage(notify_toChat, NOTIFY_MESSAGE)
		if sendResponse isOk then lastNotify_messageId = Some(sendResponse.message.messageId)
		else lastNotify_messageId = None
	}
	
	@throws[InterruptedException | IllegalArgumentException]
	private def waitToNextRoutine (): Unit = {
		Thread sleep calcNextRoutineTimestamp(System.currentTimeMillis, use_timeZone, notify_atHour)
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
