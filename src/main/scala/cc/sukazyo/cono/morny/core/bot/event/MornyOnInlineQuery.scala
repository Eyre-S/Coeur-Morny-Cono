package cc.sukazyo.cono.morny.core.bot.event

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener, InlineQueryUnit, MornyQueryManager}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.request.InlineQueryResult
import com.pengrad.telegrambot.request.AnswerInlineQuery
import com.pengrad.telegrambot.TelegramBot

import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag

class MornyOnInlineQuery (using queryManager: MornyQueryManager) (using coeur: MornyCoeur) extends EventListener {
	private given TelegramBot = coeur.account
	
	override def onInlineQuery (using event: EventEnv): Unit = {
		import event.update
		
		val results: List[InlineQueryUnit[?]] = queryManager `query` update
		
		var cacheTime = Int.MaxValue
		var isPersonal = InlineQueryUnit.defaults.IS_PERSONAL
		val resultAnswers = ListBuffer[InlineQueryResult[?]]()
		for (r <- results) {
			if (cacheTime > r.cacheTime) cacheTime = r.cacheTime
			if (r isPersonal) isPersonal = true
			resultAnswers += r.result
		}
		
		if (results isEmpty) return
		
		AnswerInlineQuery(
			update.inlineQuery.id,
			(resultAnswers toArray)*
		).cacheTime(cacheTime).isPersonal(isPersonal)
			.unsafeExecute
		
		event.setEventOk
		
	}
	
}
