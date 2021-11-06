package cc.sukazyo.cono.morny.bot;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornyTrusted;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendSticker;

import static cc.sukazyo.cono.morny.Logger.logger;

public class OnCommandExecute {
	
	private static final String ONLINE_STATUS_RETURN_STICKER_ID = "CAACAgEAAx0CW-CvvgAC5eBhhhODGRuu0pxKLwoQ3yMsowjviAACcycAAnj8xgVVU666si1utiIE";
	private static final String HELLO_STICKER_ID = "CAACAgEAAxkBAAMnYYYWKNXO4ibo9dlsmDctHhhV6fIAAqooAAJ4_MYFJJhrHS74xUAiBA";
	private static final String EXIT_STICKER_ID = "CAACAgEAAxkBAAMoYYYWt8UjvP0N405SAyvg2SQZmokAAkMiAAJ4_MYFw6yZLu06b-MiBA";
	private static final String EXIT_403_STICKER_ID = "CAACAgEAAxkBAAMqYYYa_7hpXH6hMOYMX4Nh8AVYd74AAnQnAAJ4_MYFRdmmsQKLDZgiBA";
	
	public static void searchForCommands (Update event) {
		switch (event.message().text()) {
			case "/o":
				onCommandOnExec(event);
				break;
			case "/hi":
			case "/hello":
				onCommandHelloExec(event);
				break;
			case "/exit":
				onCommandExitExec(event);
				break;
			default:
		}
	}
	
	private static void onCommandOnExec (Update event) {
		MornyCoeur.getAccount().execute(new SendSticker(
				event.message().chat().id(),
				ONLINE_STATUS_RETURN_STICKER_ID
				).replyToMessageId(event.message().messageId())
		);
	}
	
	private static void onCommandHelloExec (Update event) {
		MornyCoeur.getAccount().execute(new SendSticker(
				event.message().chat().id(),
						HELLO_STICKER_ID
				).replyToMessageId(event.message().messageId())
		);
	}
	
	private static void onCommandExitExec (Update event) {
		if (MornyTrusted.isTrusted(event.message().from().id())) {
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							EXIT_STICKER_ID
					).replyToMessageId(event.message().messageId())
			);
			logger.info("Morny exited by user @" + event.message().from().username());
			System.exit(0);
		} else {
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							EXIT_403_STICKER_ID
					).replyToMessageId(event.message().messageId())
			);
		}
	}
	
}
