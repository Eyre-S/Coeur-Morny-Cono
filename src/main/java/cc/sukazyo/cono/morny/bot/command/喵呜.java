package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SuppressWarnings("NonAsciiCharacters")
public class 喵呜 {
	
	public static class 抱抱 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "抱抱"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"抱抱——"
			));
		}
	}
	
	public static class 揉揉 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "揉揉"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"蹭蹭w"
			));
		}
	}
	
	public static class 蹭蹭 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "蹭蹭"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"喵呜~-"
			));
		}
	}
	
	public static class 贴贴 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "贴贴"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"<tg-spoiler>(贴贴喵呜&amp;.&amp;)</tg-spoiler>"
			).parseMode(ParseMode.HTML));
		}
	}
	
	public static class Progynova implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "install"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Nonnull @Override public String getParamRule () { return ""; }
		@Nonnull @Override public String getDescription () { return "抽取一个神秘盒子"; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			MornyCoeur.extra().exec(new SendSticker(
					event.message().chat().id(),
					TelegramStickers.ID_PROGYNOVA
			).replyToMessageId(event.message().messageId()));
		}
	}
	
}
