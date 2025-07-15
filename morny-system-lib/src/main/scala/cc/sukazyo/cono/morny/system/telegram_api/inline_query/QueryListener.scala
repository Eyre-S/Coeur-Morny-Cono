package cc.sukazyo.cono.morny.system.telegram_api.inline_query

import cc.sukazyo.cono.morny.system.telegram_api.event.EventEnv
import com.pengrad.telegrambot.model.InlineQuery

// TODO: docs
/** @since 0.2.0-alpha22 */
trait QueryListener {
	
	/** @since 0.2.0-alpha22 */
	def onQuery (query: InlineQuery)(using EventEnv): List[QueryResultUnit[?]] = Nil
	
}
