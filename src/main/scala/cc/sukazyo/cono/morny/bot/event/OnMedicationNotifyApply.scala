package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.daemon.MornyDaemons
import com.pengrad.telegrambot.model.{Message, Update}

object OnMedicationNotifyApply extends EventListener {
	
	override def onEditedMessage (using event: Update): Boolean =
		editedMessageProcess(event.editedMessage)
	override def onEditedChannelPost (using event: Update): Boolean =
		editedMessageProcess(event.editedChannelPost)
	
	private def editedMessageProcess (edited: Message): Boolean = {
		if edited.chat.id != MornyCoeur.config.medicationNotifyToChat then return false
		MornyDaemons.medicationTimerInstance.refreshNotificationWrite(edited)
		true
	}
	
}
