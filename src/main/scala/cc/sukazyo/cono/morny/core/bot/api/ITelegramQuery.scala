package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.MornyCoeur
import com.pengrad.telegrambot.model.Update

import javax.annotation.Nullable

trait ITelegramQuery {
	
	def query (event: Update): List[InlineQueryUnit[_]] | Null
	
}
