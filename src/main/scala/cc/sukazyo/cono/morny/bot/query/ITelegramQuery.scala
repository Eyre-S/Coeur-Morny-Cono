package cc.sukazyo.cono.morny.bot.query

import javax.annotation.Nullable
import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit
import com.pengrad.telegrambot.model.Update

trait ITelegramQuery {
	
	def query (event: Update): List[InlineQueryUnit[_]] | Null
	
}