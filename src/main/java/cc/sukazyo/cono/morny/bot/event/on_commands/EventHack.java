package cc.sukazyo.cono.morny.bot.event.on_commands;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornyTrusted;
import cc.sukazyo.cono.morny.bot.api.InputCommand;
import cc.sukazyo.cono.morny.bot.event.OnEventHackHandle;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendSticker;

/**
 * @since 0.4.2.0
 */
public class EventHack {
	
	/**
	 * @since 0.4.2.0
	 */
	public static void exec (Update event, InputCommand command) {
		
		boolean isOk = false;
		
		String x_mode = "";
		if (command.hasArgs()) {
			x_mode = command.getArgs()[0];
		}
		
		switch (x_mode) {
			case "any":
				if (MornyTrusted.isTrusted(event.message().from().id())) {
					OnEventHackHandle.registerHack(
							event.message().messageId(),
							event.message().from().id(),
							event.message().chat().id(),
							OnEventHackHandle.HackType.ANY
					);
					isOk = true;
				}
				break;
			case "group":
				OnEventHackHandle.registerHack(
						event.message().messageId(),
						event.message().from().id(),
						event.message().chat().id(),
						OnEventHackHandle.HackType.GROUP
				);
				isOk = true;
				break;
			default:
				OnEventHackHandle.registerHack(
						event.message().messageId(),
						event.message().from().id(),
						event.message().chat().id(),
						OnEventHackHandle.HackType.USER
				);
				isOk = true;
				break;
		}
		
		if (isOk) {
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_WAITING
					).replyToMessageId(event.message().messageId())
			);
		} else {
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_403
					).replyToMessageId(event.message().messageId())
			);
		}
		
	}
	
}
