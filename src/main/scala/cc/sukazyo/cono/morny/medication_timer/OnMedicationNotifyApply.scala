package cc.sukazyo.cono.morny.medication_timer

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}
import com.pengrad.telegrambot.model.Message

class OnMedicationNotifyApply (using instance: MedicationTimer)(using coeur: MornyCoeur) extends EventListener {
	
	override def onEditedMessage (using event: EventEnv): Unit =
		editedMessageProcess(event.update.editedMessage)
	override def onEditedChannelPost (using event: EventEnv): Unit =
		editedMessageProcess(event.update.editedChannelPost)
	
	private def editedMessageProcess (edited: Message)(using event: EventEnv): Unit = {
		if edited.chat.id != coeur.config.medicationNotifyToChat then return;
		if instance.refreshNotificationWrite(edited) then
			event.setEventOk
	}
	
}
