package cc.sukazyo.cono.morny.core.bot.api

import com.pengrad.telegrambot.model.Update

trait ITelegramQuery {
	
	def query (event: Update): List[InlineQueryUnit[?]] | Null
	
}
