package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.MornyCoeur
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class MornyQueryManager (using MornyCoeur) {
	
	private val queries = mutable.Queue.empty[ITelegramQuery]
	
	infix def register (query: ITelegramQuery): Unit =
		this.queries += query
	
	def register (queries: ITelegramQuery*): Unit =
		this.queries ++= queries
	
	def query (event: Update): List[InlineQueryUnit[?]] = {
		val results = ListBuffer[InlineQueryUnit[?]]()
		for (instance <- queries) {
			val r = instance `query` event
			if (r != null) results ++= r
		}
		results.result()
	}
	
}
