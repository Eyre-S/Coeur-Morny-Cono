package cc.sukazyo.cono.morny.bot.query

import com.pengrad.telegrambot.model.Update

trait ITelegramQuery {
	
	def query (event: Update): List[InlineQueryUnit[_]] | Null
	
}
