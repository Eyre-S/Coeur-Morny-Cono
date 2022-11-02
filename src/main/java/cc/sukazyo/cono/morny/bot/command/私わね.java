package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("NonAsciiCharacters")
public class 私わね implements ISimpleCommand {
	
	@Nonnull
	@Override public String getName () { return "me"; }
	
	@Nullable
	@Override public String[] getAliases () { return null; }
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		if (ThreadLocalRandom.current().nextInt(521) == 0) {
			// 可以接入未来的心情系统（如果有的话）
			final String text = switch (ThreadLocalRandom.current().nextInt(11)) {
				case 0,7,8,9,10 -> "才不是";
				case 1,2,3,6 -> "才不是！";
				case 4,5 -> "才不是..";
				default -> throw new IllegalStateException("Unexpected random value in 私わね command.");
			};
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					text
			).replyToMessageId(event.message().messageId()));
		}
	}
	
}
