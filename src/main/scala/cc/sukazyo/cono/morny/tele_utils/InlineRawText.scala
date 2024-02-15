package cc.sukazyo.cono.morny.tele_utils

import cc.sukazyo.cono.morny.core.bot.api.{InlineQueryUnit, ITelegramQuery}
import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent}

import scala.language.postfixOps

class InlineRawText extends ITelegramQuery {
	
	private val ID_PREFIX = "[morny/r/text]"
	private val TITLE = "Raw Text"
	
	override def query (event: Update): List[InlineQueryUnit[?]] | Null = {
		
		if (event.inlineQuery.query == null || (event.inlineQuery.query isBlank)) return null
		
		List(
			InlineQueryUnit(InlineQueryResultArticle(
				inlineQueryId(ID_PREFIX, event.inlineQuery.query), TITLE,
				InputTextMessageContent(event.inlineQuery.query)
			))
		)
		
	}
	
}
