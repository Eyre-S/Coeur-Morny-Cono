package cc.sukazyo.cono.morny.tele_utils

import cc.sukazyo.cono.morny.system.telegram_api.event.EventEnv
import cc.sukazyo.cono.morny.system.telegram_api.formatting.NamingUtils.inlineQueryId
import cc.sukazyo.cono.morny.system.telegram_api.inline_query.{QueryListener, QueryResultUnit}
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent}

import scala.language.postfixOps

class InlineRawText extends QueryListener {
	
	private val ID_PREFIX = "[morny/r/text]"
	private val TITLE = "Raw Text"
	
	override def onQuery (query: InlineQuery)(using EventEnv): List[QueryResultUnit[?]] = {
		
		if (query.query == null || (query.query isBlank)) return Nil
		
		List(
			QueryResultUnit(InlineQueryResultArticle(
				inlineQueryId(ID_PREFIX, query.query), TITLE,
				InputTextMessageContent(query.query)
			))
		)
		
	}
	
}
