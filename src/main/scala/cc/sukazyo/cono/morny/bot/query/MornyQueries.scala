package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.bot.query
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable.ListBuffer

object MornyQueries {
	
	private val queryInstances = Set[ITelegramQuery](
		RawText,
		MyInformation,
		ShareToolTwitter,
		ShareToolBilibili
	)
	
	def query (event: Update): List[InlineQueryUnit[_]] = {
		val results = ListBuffer[InlineQueryUnit[_]]()
		for (instance <- queryInstances) {
			val r = instance query event
			if (r != null) results ++= r
		}
		results.result()
	}
	
}
