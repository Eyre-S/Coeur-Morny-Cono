package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.MornyCoeur
import com.pengrad.telegrambot.model.Update

import javax.annotation.Nullable

trait ITelegramQuery {
	
	def query (event: Update): List[InlineQueryUnit[_]] | Null
	
}
