package cc.sukazyo.cono.morny.system.telegram_api.inline_query

import cc.sukazyo.cono.morny.system.telegram_api.inline_query.InlineQueryUnit.defaults
import com.pengrad.telegrambot.model.request.InlineQueryResult

object InlineQueryUnit {
	
	object defaults:
		val CACHE_TIME = 300
		val IS_PERSONAL = false
	
}

class InlineQueryUnit[T <: InlineQueryResult[T]](val result: T) {
	
	var cacheTime: Int = defaults.CACHE_TIME
	var isPersonal: Boolean = defaults.IS_PERSONAL
	
	def cacheTime (v: Int): InlineQueryUnit[T] =
		this.cacheTime = v
		this
	
	def isPersonal (v: Boolean): InlineQueryUnit[T] =
		this.isPersonal = v
		this
	
}
