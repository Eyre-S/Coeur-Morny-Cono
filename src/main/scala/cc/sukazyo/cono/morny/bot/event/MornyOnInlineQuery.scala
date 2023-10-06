package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.bot.query.{InlineQueryUnit, MornyQueries}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResult
import com.pengrad.telegrambot.request.AnswerInlineQuery

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import scala.reflect.ClassTag

class MornyOnInlineQuery (using queryManager: MornyQueries) (using coeur: MornyCoeur) extends EventListener {
	
	override def onInlineQuery (using update: Update): Boolean = {
		
		val results: List[InlineQueryUnit[_]] = queryManager query update
		
		var cacheTime = Int.MaxValue
		var isPersonal = InlineQueryUnit.defaults.IS_PERSONAL
		val resultAnswers = ListBuffer[InlineQueryResult[_]]()
		for (r <- results) {
			if (cacheTime > r.cacheTime) cacheTime = r.cacheTime
			if (r isPersonal) isPersonal = true
			resultAnswers += r.result
		}
		
		if (results isEmpty) return false
		
		coeur.account exec AnswerInlineQuery(
			update.inlineQuery.id, resultAnswers toArray:_*
		).cacheTime(cacheTime).isPersonal(isPersonal)
		true
		
	}
	
}