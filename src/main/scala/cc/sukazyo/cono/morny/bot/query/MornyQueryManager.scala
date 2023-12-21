package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.bot.query
import cc.sukazyo.cono.morny.MornyCoeur
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class MornyQueryManager (using MornyCoeur) {
	
	private val queries = mutable.Queue.empty[ITelegramQuery]
	
	def register (queries: ITelegramQuery*): Unit =
		this.queries ++= queries
	
	def query (event: Update): List[InlineQueryUnit[_]] = {
		val results = ListBuffer[InlineQueryUnit[_]]()
		for (instance <- queries) {
			val r = instance query event
			if (r != null) results ++= r
		}
		results.result()
	}
	
}
