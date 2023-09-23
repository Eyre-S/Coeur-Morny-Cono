package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.daemon.{MedicationTimer, MornyDaemons}
import com.pengrad.telegrambot.model.{Message, Update}

class OnMedicationNotifyApply (using coeur: MornyCoeur) extends EventListener {
	
	override def onEditedMessage (using event: Update): Boolean =
		editedMessageProcess(event.editedMessage)
	override def onEditedChannelPost (using event: Update): Boolean =
		editedMessageProcess(event.editedChannelPost)
	
	private def editedMessageProcess (edited: Message): Boolean = {
		if edited.chat.id != coeur.config.medicationNotifyToChat then return false
		coeur.daemons.medicationTimer.refreshNotificationWrite(edited)
		true
	}
	
}
