package cc.sukazyo.cono.morny.system.telegram_api.inline_query

import cc.sukazyo.cono.morny.system.telegram_api.inline_query.QueryResultUnit.defaults
import com.pengrad.telegrambot.model.request.InlineQueryResult
import com.pengrad.telegrambot.request.AnswerInlineQuery

import scala.collection.mutable.ListBuffer

// TODO: docs
/** @since 0.2.0-alpha22 */
object QueryResultUnit {
	
	/** @since 0.2.0-alpha22 */
	object defaults:
		/** @since 0.2.0-alpha22 */
		val CACHE_TIME = 300
		/** @since 0.2.0-alpha22 */
		val IS_PERSONAL = false
	
	/** @since 0.2.0-alpha22 */
	def foldAnswerFrom
	(inlineQueryId: String, queryResults: List[QueryResultUnit[?]])(defaultCacheTime: Int)
	:AnswerInlineQuery = {
		
		var cacheTime = defaultCacheTime
		var isPersonal = QueryResultUnit.defaults.IS_PERSONAL
		val resultAnswers = ListBuffer[InlineQueryResult[?]]()
		for (r <- queryResults) {
			if (cacheTime > r.cacheTime) cacheTime = r.cacheTime
			if (r isPersonal) isPersonal = true
			resultAnswers += r.result
		}
		
		AnswerInlineQuery(
			inlineQueryId,
			resultAnswers.toList *,
		).isPersonal(isPersonal).cacheTime(cacheTime)
		
	}
	
}

// TODO: docs
/** @since 0.2.0-alpha22 */
class QueryResultUnit[T <: InlineQueryResult[T]](val result: T) {
	
	/** @since 0.2.0-alpha22 */
	var cacheTime: Int = defaults.CACHE_TIME
	/** @since 0.2.0-alpha22 */
	var isPersonal: Boolean = defaults.IS_PERSONAL
	
	/** @since 0.2.0-alpha22 */
	def cacheTime (v: Int): QueryResultUnit[T] =
		this.cacheTime = v
		this
	
	/** @since 0.2.0-alpha22 */
	def isPersonal (v: Boolean): QueryResultUnit[T] =
		this.isPersonal = v
		this
	
}
