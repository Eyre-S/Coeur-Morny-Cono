package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.tgapi.ExtraAction;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MornyInformations implements ITelegramCommand {
	
	private static final String SUB_STICKER = "stickers";
	private static final String SUB_RUNTIME = "runtime";
	private static final String SUB_VERSION = "version";
	private static final String SUB_VERSION_2 = "v";
	
	@Nonnull @Override public String getName () { return "info"; }
	@Nullable @Override public String[] getAliases () { return new String[0]; }
	@Nonnull @Override public String getParamRule () { return "[subcommand]"; }
	@Nonnull @Override public String getDescription () { return "输出当前 Morny 的各种信息"; }
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		if (!command.hasArgs()) {
			MornyCommands.onCommandRuntimeExec(event);
			return;
		}
		
		final String action = command.getArgs()[0];
		
		if (action.startsWith(SUB_STICKER)) {
			echoStickers(command, event);
		} else if (action.equals(SUB_RUNTIME)) {
			echoRuntime(command, event);
		} else if (action.equals(SUB_VERSION) || action.equals(SUB_VERSION_2)) {
			echoVersion(command, event);
		} else {
			echo404(event);
		}
		
	}
	
	/**
	 * /info 子命令 {@value #SUB_STICKER}
	 */
	public void echoStickers (@Nonnull InputCommand command, @Nonnull Update event) {
		final long echoTo = event.message().chat().id();
		final int replyToMessage = event.message().messageId();
		String id = null;
		if (command.getArgs()[0].equals(SUB_STICKER)) {
			if (command.getArgs().length == 1) {
				id = "";
			} else if (command.getArgs().length == 2) {
				id = command.getArgs()[1];
			}
		} else if (command.getArgs().length == 1) {
			if (command.getArgs()[0].startsWith(SUB_STICKER+".") || command.getArgs()[0].startsWith(SUB_STICKER+"#")) {
				id = command.getArgs()[0].substring(SUB_STICKER.length()+1);
			}
		}
		if (id == null) { echo404(event); return; }
		echoStickers(id, echoTo, replyToMessage);
	}
	
	/**
	 * 向 telegram 输出一个或全部 sticker
	 * @param id
	 *        sticker 在 {@link TelegramStickers} 中的字段名。
	 *        使用 {@link ""}(空字符串)(不是{@link null}) 表示输出全部 sticker
	 * @param chatId 目标 chat id
	 * @param messageId 要回复的消息 id，特殊值跟随上游逻辑
	 * @see TelegramStickers#echoStickerByID(String, ExtraAction, long, int)
	 * @see TelegramStickers#echoAllStickers(ExtraAction, long, int)
	 */
	public static void echoStickers (@Nonnull String id, long chatId, int messageId) {
		if ("".equals(id)) TelegramStickers.echoAllStickers(MornyCoeur.extra(), chatId, messageId);
		else TelegramStickers.echoStickerByID(id, MornyCoeur.extra(), chatId, messageId);
	}
	
	/**
	 * /info 子命令 {@value #SUB_RUNTIME}
	 * @since 1.0.0-alpha4
	 */
	public static void echoRuntime (@Nonnull InputCommand command, @Nonnull Update event) {
		if (command.getArgs().length == 1)
			MornyCommands.onCommandRuntimeExec(event);
		else echo404(event);
	}
	
	/**
	 * /info 子命令 {@value #SUB_VERSION}
	 * @since 1.0.0-alpha4
	 */
	public static void echoVersion (@Nonnull InputCommand command, @Nonnull Update event) {
		if (command.getArgs().length == 1)
			MornyCommands.onCommandVersionExec(event);
		else echo404(event);
	}
	
	private static void echo404 (@Nonnull Update event) {
		MornyCoeur.extra().exec(new SendSticker(
				event.message().chat().id(),
				TelegramStickers.ID_404
		).replyToMessageId(event.message().messageId()));
	}
	
}
