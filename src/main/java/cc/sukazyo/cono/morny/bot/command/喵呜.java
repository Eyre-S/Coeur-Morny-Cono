package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * <b>WARNING</b> that {@link cc.sukazyo.cono.morny.bot.event.OnTelegramCommand}
 * 并不能够处理非 english word 字符之外的命令.
 * <p>
 * 出于这个限制，以下几个命令目前都无法使用
 * @see 抱抱
 * @see 揉揉
 * @see 蹭蹭
 * @see 贴贴
 */
@SuppressWarnings("NonAsciiCharacters")
public class 喵呜 {
	
	public static class 抱抱 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "抱抱"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			replyingSet(event, "抱抱", "抱抱");
		}
	}
	
	public static class 揉揉 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "揉揉"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			replyingSet(event, "蹭蹭", "摸摸");
		}
	}
	
	public static class 蹭蹭 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "蹭蹭"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			replyingSet(event, "揉揉", "蹭蹭");
		}
	}
	
	public static class 贴贴 implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "贴贴"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
			replyingSet(event, "贴贴", "贴贴");
		}
	}
	
	private static void replyingSet (@Nonnull Update event, @Nonnull String whileRec, @Nonnull String whileNew) {
		final boolean isNew = event.message().replyToMessage() == null;
		final Message target = isNew ? event.message() : event.message().replyToMessage();
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				isNew ? whileNew : whileRec
		).replyToMessageId(target.messageId()).parseMode(ParseMode.HTML));
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
