package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MornyInformations implements ITelegramCommand {
	
	private static final String ACT_STICKER = "stickers";
	
	@Nonnull @Override public String getName () { return "info"; }
	@Nullable @Override public String[] getAliases () { return new String[0]; }
	@Nonnull @Override public String getParamRule () { return "[(stickers)|(stickers.)sticker_id]"; }
	@Nonnull @Override public String getDescription () { return "输出 Morny 当前版本的一些预定义信息"; }
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		if (!command.hasArgs() || command.getArgs().length > 1) {
			MornyCoeur.extra().exec(new SendSticker(event.message().chat().id(), TelegramStickers.ID_404).replyToMessageId(event.message().messageId()));
		}
		
		final String action = command.getArgs()[0];
		
		if (action.startsWith("stickers")) {
			if (action.equals("stickers"))
				TelegramStickers.echoAllStickers(MornyCoeur.extra(), event.message().chat().id(), event.message().messageId());
			else {
				TelegramStickers.echoStickerByID(
						action.substring((ACT_STICKER+".").length()),
						MornyCoeur.extra(), event.message().chat().id(), event.message().messageId()
				);
			}
			return;
		}
		
		MornyCoeur.extra().exec(new SendSticker(event.message().chat().id(), TelegramStickers.ID_404).replyToMessageId(event.message().messageId()));
		
	}
	
}
