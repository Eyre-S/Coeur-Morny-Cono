package cc.sukazyo.cono.morny.system.telegram_api.account

import com.pengrad.telegrambot.TelegramBot

class StaticBotAccount (telegramBot: TelegramBot) extends BotAccount {
	
	/** Get the [[TelegramBot]] instance associated to this account.
	  *
	  * For a simple usage, it will always return a fixed [[TelegramBot]] instance. But for
	  * account implementations that has load-balance features, it may return different
	  * [[TelegramBot]] for different calls.
	  */
	override def getTelegramBot: TelegramBot = telegramBot
	
}
