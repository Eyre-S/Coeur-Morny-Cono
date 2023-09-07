package cc.sukazyo.cono.morny.bot.query
import cc.sukazyo.cono.morny.util.tgapi.formatting.NamedUtils.inlineIds
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent}

import scala.language.postfixOps

object RawText extends ITelegramQuery {
	
	private val ID_PREFIX = "[morny/r/text]"
	private val TITLE = "Raw Text"
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null || (event.inlineQuery.query isBlank)) return null
		
		List(
			InlineQueryUnit(InlineQueryResultArticle(
				inlineIds(ID_PREFIX, event.inlineQuery.query), TITLE,
				InputTextMessageContent(event.inlineQuery.query)
			))
		)
		
	}
	
}
