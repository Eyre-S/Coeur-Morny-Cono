package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornyTrusted;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import org.jetbrains.annotations.NotNull;

/**
 * @since 0.4.2.1
 */
public class OnCallMe extends EventListener {
	
	/**
	 * @since 0.4.2.1
	 */
	private static final long ME = MornyTrusted.MASTER;
	
	@Override
	public boolean onMessage (@NotNull Update update) {
		if (update.message().text() == null)
			return false;
		if (update.message().chat().type() != Chat.Type.Private)
			return false;
		switch (update.message().text().toLowerCase()) {
			case "steam":
			case "sbeam":
			case "sdeam":
				requestSteamJoin(update);
				break;
			case "hana paresu":
			case "花宫":
			case "内群":
				requestHanaParesuJoin(update);
				break;
			default:
				return false;
		}
		MornyCoeur.getAccount().execute(new SendSticker(
						update.message().chat().id(),
						TelegramStickers.ID_SENT
				).replyToMessageId(update.message().messageId())
		);
		return true;
	}
	
	private static void requestSteamJoin (Update event) {
		MornyCoeur.getAccount().execute(new SendMessage(
				ME, String.format("""
						request <b>STEAM LIBRARY</b>
						from <a href="tg://user?id=%d">%s</a>""",
						event.message().from().id(),
						event.message().from().firstName() + " " + event.message().from().lastName()
				)
		).parseMode(ParseMode.HTML));
	}
	
	private static void requestHanaParesuJoin (Update event) {
		MornyCoeur.getAccount().execute(new SendMessage(
				ME, String.format("""
						request <b>Hana Paresu</b>
						from <a href="tg://user?id=%d">%s</a>""",
						event.message().from().id(),
						event.message().from().firstName() + " " + event.message().from().lastName()
				)
		).parseMode(ParseMode.HTML));
	}
	
}
