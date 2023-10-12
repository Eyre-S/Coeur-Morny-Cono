package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.daemon.{MedicationTimer, MornyDaemons}
import com.pengrad.telegrambot.model.{Message, Update}

class OnMedicationNotifyApply (using coeur: MornyCoeur) extends EventListener {
	
	override def onEditedMessage (using event: EventEnv): Unit =
		editedMessageProcess(event.update.editedMessage)
	override def onEditedChannelPost (using event: EventEnv): Unit =
		editedMessageProcess(event.update.editedChannelPost)
	
	private def editedMessageProcess (edited: Message)(using event: EventEnv): Unit = {
		if edited.chat.id != coeur.config.medicationNotifyToChat then return;
		coeur.daemons.medicationTimer.refreshNotificationWrite(edited)
		event.setEventOk
	}
	
}
