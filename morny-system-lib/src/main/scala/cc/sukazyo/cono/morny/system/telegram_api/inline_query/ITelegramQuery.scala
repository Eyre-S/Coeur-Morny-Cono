package cc.sukazyo.cono.morny.system.telegram_api.inline_query

import com.pengrad.telegrambot.model.Update

trait ITelegramQuery {
	
	def query (event: Update): List[InlineQueryUnit[?]] | Null
	
}
