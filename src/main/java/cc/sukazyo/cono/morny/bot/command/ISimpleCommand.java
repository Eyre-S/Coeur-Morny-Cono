package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.untitled.util.telegram.object.InputCommand;
import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISimpleCommand {
	
	@Nonnull
	String getName();
	
	@Nullable
	String[] getAliases();
	
	void execute (@Nonnull InputCommand command, @Nonnull Update event);
	
}
