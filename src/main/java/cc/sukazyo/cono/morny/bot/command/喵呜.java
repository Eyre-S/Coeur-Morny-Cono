package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.untitled.util.telegram.object.InputCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("NonAsciiCharacters")
public class 喵呜 {
	
	public static class 抱抱 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "/抱抱"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"抱抱——"
			));
		}
	}
	
	public static class 揉揉 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "/揉揉"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"蹭蹭w"
			));
		}
	}
	
	public static class 蹭蹭 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "/蹭蹭"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"喵呜~-"
			));
		}
	}
	
	public static class 贴贴 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "/贴贴"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"<tg-spoiler>(贴贴喵呜&amp;.&amp;)</tg-spoiler>"
			).parseMode(ParseMode.HTML));
		}
	}
	
}
