package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.untitled.util.telegram.object.InputCommand;
import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITelegramCommand {
	
	@Nonnull
	String getName();
	
	@Nullable
	String[] getAliases();
	@Nonnull
	String getParamRule();
	@Nonnull
	String getDescription();
	
	void execute (@Nonnull InputCommand command, @Nonnull Update event);
	
}
