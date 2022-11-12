package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Testing implements ISimpleCommand {
	
	@Nonnull
	@Override
	public String getName () {
		return "test";
	}
	
	@Nullable
	@Override
	public String[] getAliases () {
		return null;
	}
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				"<b>Just</b> a TEST command."
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
		
	}
	
}
