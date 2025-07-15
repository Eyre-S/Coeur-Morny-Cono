package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.event.EventEnv
import cc.sukazyo.cono.morny.system.telegram_api.inline_query.{QueryListener, QueryResultUnit}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class MornyQueryManager (using MornyCoeur) {
	
	private val queries = mutable.Queue.empty[QueryListener]
	
	infix def register (query: QueryListener): Unit =
		this.queries += query
	
	def register (queries: QueryListener*): Unit =
		this.queries ++= queries
	
	def onInlineQuery (listenerExceptionCallback: (Throwable, QueryListener)=>Any)(using event: EventEnv): List[QueryResultUnit[?]] = {
		val results = ListBuffer[QueryResultUnit[?]]()
		for (instance <- queries) {
			try
				val r = instance.onQuery(event.update.inlineQuery)
				if (r != null) results ++= r
			catch case e => listenerExceptionCallback(e, instance)
		}
		results.result()
	}
	
}
