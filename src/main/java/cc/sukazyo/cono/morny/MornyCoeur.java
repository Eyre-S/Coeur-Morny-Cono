package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.bot.api.OnUpdate;
import cc.sukazyo.cono.morny.bot.event.EventListeners;
import cc.sukazyo.cono.morny.data.MornyHello;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetMe;

import static cc.sukazyo.cono.morny.Logger.logger;

public class MornyCoeur {
	
	private static TelegramBot account;
	public static final String USERNAME = "morny_cono_annie_bot";
	
	public static void main (String[] args) {
		
		logger.info(MornyHello.MORNY_PREVIEW_IMAGE_ASCII);
		logger.info("System Starting");
		
		logger.info("args key:\n  " + args[0]);
		
		try { account = login(args[0]); }
		catch (Exception e) { logger.error("Cannot login to bot/api. :\n  " + e.getMessage()); System.exit(-1); }
		
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
				String username = account.execute(new GetMe()).user().username();
				if (!USERNAME.equals(username))
					throw new RuntimeException("Required the bot @"+USERNAME + " but @"+username + "logged in!");
				logger.info("Succeed login to @" + username);
				return account;
			} catch (Exception e) {
				e.printStackTrace(System.out);
				logger.error("login failed.");
			}
		}
		throw new RuntimeException("Login failed..");
	}
	
	public static TelegramBot getAccount () {
		return account;
	}
	
}
