package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.bot.query
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.EventEnv
import cc.sukazyo.cono.morny.bot.query.MornyQueries.QueryListenerExceptionListener
import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable.ListBuffer

object MornyQueries {
	
	type QueryListenerExceptionListener = (ITelegramQuery, Throwable)=>Any
	
	class QueryListenerFailed (ex: Throwable, val queryListener: ITelegramQuery, event: EventEnv)
	extends EventRuntimeException.EventAboutFailed(
		ex, s"Unexcepted exception occurred on sub-query ${queryListener.getClass.getName}"
	)(event)
	
}

class MornyQueries (using MornyCoeur) {
	
	private val queryInstances = Set[ITelegramQuery](
		RawText(),
		MyInformation(),
		ShareToolTwitter(),
		ShareToolBilibili(),
		ShareToolXhs(),
		ShareToolSocialContent(),
		GenError()
	)
	
	def query (onQueryListenerExceptions: QueryListenerExceptionListener)(event: Update): List[InlineQueryUnit[_]] = {
		val results = ListBuffer[InlineQueryUnit[_]]()
		for (instance <- queryInstances) {
			try {
				val r = instance query event
				if (r != null) results ++= r
			} catch case e => onQueryListenerExceptions(instance, e)
		}
		results.result()
	}
	
}
