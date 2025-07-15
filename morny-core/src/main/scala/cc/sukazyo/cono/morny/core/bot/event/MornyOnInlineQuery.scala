package cc.sukazyo.cono.morny.core.bot.event

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.MornyQueryManager
import cc.sukazyo.cono.morny.core.event.TelegramBotEvents
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.system.telegram_api.inline_query.{QueryListener, QueryResultUnit}
import com.pengrad.telegrambot.TelegramBot

class MornyOnInlineQuery (using queryManager: MornyQueryManager) (using coeur: MornyCoeur) extends EventListener {
	private given TelegramBot = coeur.account
	
	private def onQueryListenerException (event: EventEnv)(e: Throwable, queryListener: QueryListener): Unit = {
		// TODO: logs here
		// TODO: reporter should listener this and do report
		TelegramBotEvents.inCoeur.OnQueryListenerOccursException.emit(
			e, queryListener, event
		)
	}
	
	override def onInlineQuery (using event: EventEnv): Unit = {
		import event.update
		
		val results: List[QueryResultUnit[?]] = queryManager.onInlineQuery(onQueryListenerException(event))
		if (results isEmpty) return
		
		QueryResultUnit.foldAnswerFrom(update.inlineQuery.id, results)(
			if (coeur.config.debugMode) 0
			else coeur.config.inlineQueryCacheTimeMax
		).unsafeExecute
		
		event.setEventOk
		
	}
	
}
