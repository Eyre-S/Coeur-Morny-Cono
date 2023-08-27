package cc.sukazyo.cono.morny.bot.command;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendPhoto;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * The implementation of Telegram special command `/start`.
 *
 * @see MornyInformation related class where some data comes from.
 *
 * @since 1.0.0-RC4
 */
public class MornyInfoOnHello implements ISimpleCommand {

	@Nonnull @Override public String getName() { return "start"; }
	@Nullable @Override public String[] getAliases() { return new String[0]; }
	// @Override public String getParamRule() { return ""; }
	// @Override public String getDescription() { return "" }

	@Override
	public void execute(@Nonnull InputCommand command, @Nonnull Update event) {
		MornyCoeur.extra().exec(new SendPhoto(
				event.message().chat().id(),
				MornyInformation.getAboutPic()
		).caption("""
				欢迎使用 <b>Morny Cono</b>，<i>来自安妮的侍从小鼠</i>。
				Morny 具有各种各样的功能。
				
				————————————————
				%s
				————————————————
				
				（你可以随时通过 /info 重新获得这些信息）""".formatted(MornyInformation.getMornyAboutLinksHTML())
		).parseMode(ParseMode.HTML));
	}
	
}
