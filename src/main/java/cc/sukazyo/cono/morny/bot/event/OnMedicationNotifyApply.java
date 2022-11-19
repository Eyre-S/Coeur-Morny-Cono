package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.daemon.MornyDaemons;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;

public class OnMedicationNotifyApply extends EventListener {
	
	@Override
	public boolean onEditedChannelPost (@Nonnull Update update) {
		return editedMessageProcess(update.editedChannelPost());
	}
	
	@Override
	public boolean onEditedMessage (@Nonnull Update update) {
		return editedMessageProcess(update.editedMessage());
	}
	
	private boolean editedMessageProcess (Message edited) {
		if (edited.chat().id() != MornyCoeur.config().medicationNotifyToChat) return false;
		MornyDaemons.medicationTimerInstance.refreshNotificationWrite(edited);
		return true;
	}
	
}
