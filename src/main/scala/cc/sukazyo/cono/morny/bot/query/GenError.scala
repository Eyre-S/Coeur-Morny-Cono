package cc.sukazyo.cono.morny.bot.query

import com.pengrad.telegrambot.model.Update

class GenError extends ITelegramQuery {
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if event.inlineQuery.query != "!!!gen-err" then return null
		
		throw RuntimeException("Test exception!")
		
	}
	
}
