package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornySystem;
import cc.sukazyo.cono.morny.MornyTrusted;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.bot.event.on_commands.GetUsernameAndId;
import cc.sukazyo.cono.morny.util.StringUtils;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import static cc.sukazyo.cono.morny.Logger.logger;

public class OnCommandExecute extends EventListener {
	
	private static final String ONLINE_STATUS_RETURN_STICKER_ID = "CAACAgEAAx0CW-CvvgAC5eBhhhODGRuu0pxKLwoQ3yMsowjviAACcycAAnj8xgVVU666si1utiIE";
	private static final String HELLO_STICKER_ID = "CAACAgEAAxkBAAMnYYYWKNXO4ibo9dlsmDctHhhV6fIAAqooAAJ4_MYFJJhrHS74xUAiBA";
	private static final String EXIT_STICKER_ID = "CAACAgEAAxkBAAMoYYYWt8UjvP0N405SAyvg2SQZmokAAkMiAAJ4_MYFw6yZLu06b-MiBA";
	private static final String EXIT_403_STICKER_ID = "CAACAgEAAxkBAAMqYYYa_7hpXH6hMOYMX4Nh8AVYd74AAnQnAAJ4_MYFRdmmsQKLDZgiBA";
	
	@Override
	public boolean onMessage (Update event) {
		if (event.message().text() == null) {
			return false;
		}
		String[] command = StringUtils.formatCommand(event.message().text());
		if (command.length == 0) return false;
		switch (command[0]) {
			case "/user":
			case "/user@" + MornyCoeur.USERNAME:
				GetUsernameAndId.exec(command, event);
				break;
			case "/o":
			case "/o@" + MornyCoeur.USERNAME:
				onCommandOnExec(event);
				break;
			case "/hi":
			case "/hi@" + MornyCoeur.USERNAME:
			case "/hello":
			case "/hello@" + MornyCoeur.USERNAME:
				onCommandHelloExec(event);
				break;
			case "/exit":
			case "/exit@" + MornyCoeur.USERNAME:
				onCommandExitExec(event);
				break;
			case "/version":
			case "/version@" + MornyCoeur.USERNAME:
				onCommandVersionExec(event);
				break;
			default:
				return false;
		}
		return true;
	}
	
	private void onCommandOnExec (Update event) {
		MornyCoeur.getAccount().execute(new SendSticker(
				event.message().chat().id(),
				ONLINE_STATUS_RETURN_STICKER_ID
				).replyToMessageId(event.message().messageId())
		);
	}
	
	private void onCommandHelloExec (Update event) {
		MornyCoeur.getAccount().execute(new SendSticker(
				event.message().chat().id(),
						HELLO_STICKER_ID
				).replyToMessageId(event.message().messageId())
		);
	}
	
	private void onCommandExitExec (Update event) {
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
			logger.info("403 exited tag from user @" + event.message().from().username());
		}
	}
	
	private void onCommandVersionExec (Update event) {
		MornyCoeur.getAccount().execute(new SendMessage(
				event.message().chat().id(),
				String.format(
						"version:\n" +
						"\t<code>%s</code>\n" +
						"core md5_hash:\n" +
						"\t<code>%s</code>",
						MornySystem.VERSION,
						MornySystem.getJarMd5()
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
}
