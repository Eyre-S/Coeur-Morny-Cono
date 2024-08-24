package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.bot.query.{InlineQueryUnit, MornyQueries}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.Log.logger
import com.google.gson.Gson
import com.pengrad.telegrambot.model.request.InlineQueryResult
import com.pengrad.telegrambot.request.AnswerInlineQuery

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.reflect.ClassTag

class MornyOnInlineQuery (using queryManager: MornyQueries) (using coeur: MornyCoeur) extends EventListener {
	
	override def onInlineQuery (using event: EventEnv): Unit = {
		import event.update
		
		val results: List[InlineQueryUnit[_]] = queryManager query update
		
		var cacheTime = Int.MaxValue
		var isPersonal = InlineQueryUnit.defaults.IS_PERSONAL
		val resultAnswers = ListBuffer[InlineQueryResult[_]]()
		for (r <- results) {
			if (cacheTime > r.cacheTime) cacheTime = r.cacheTime
			if (r isPersonal) isPersonal = true
			resultAnswers += r.result
		}
		cacheTime = 1
		logger debug "Inline Query remote caches is DISABLED, you may received duplicate queries logs."
		
		if (results isEmpty) return;
		
		logger trace "Query answers:\n" + resultAnswers.map("  " + Gson().toJson(_)).mkString("\n")
		coeur.account exec AnswerInlineQuery(
			update.inlineQuery.id, resultAnswers toArray:_*
		).cacheTime(cacheTime).isPersonal(isPersonal)
		
		event.setEventOk
		
	}
	
}
