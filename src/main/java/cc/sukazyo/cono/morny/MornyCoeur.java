package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.bot.api.OnUpdate;
import cc.sukazyo.cono.morny.bot.event.EventListeners;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetMe;

import static cc.sukazyo.cono.morny.Logger.logger;

public class MornyCoeur {
	
	private static TelegramBot account;
	
	public static void main (String[] args) {
		
		logger.info("System Starting");
		
		logger.info("args key: " + args[0]);
		
		account = login(args[0]);
		
		logger.info("Bot login succeed.");
		
		EventListeners.registerAllListeners();
		account.setUpdatesListener(OnUpdate::onNormalUpdate);
		
		logger.info("System start complete");
		
	}
	
	public static TelegramBot login (String key) {
		TelegramBot account = new TelegramBot(key);
		logger.info("Trying to login...");
		for (int i = 1; i < 4; i++) {
			if (i != 1) logger.info("retrying...");
			try {
				logger.info("Succeed login to @" + account.execute(new GetMe()).user().username());
				return account;
			} catch (Exception e) {
				e.printStackTrace(System.out);
				logger.info("login failed.");
			}
		}
		throw new RuntimeException("Login failed..");
	}
	
	public static TelegramBot getAccount () {
		return account;
	}
	
}
