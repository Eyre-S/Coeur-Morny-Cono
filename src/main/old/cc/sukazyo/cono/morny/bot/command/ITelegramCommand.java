package cc.sukazyo.cono.morny.bot.command;

import javax.annotation.Nonnull;

public interface ITelegramCommand extends ISimpleCommand {
	
	@Nonnull
	String getParamRule();
	@Nonnull
	String getDescription();
	
}
