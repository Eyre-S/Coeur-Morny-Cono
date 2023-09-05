package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit
import cc.sukazyo.cono.morny.util.tgapi.formatting.NamedUtils.inlineIds
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramUserInformation
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent, ParseMode}

import scala.language.postfixOps

object MyInformation extends ITelegramQuery {
	
	private val ID_PREFIX = "[morny/info/me]"
	private val TITLE = "My Account Information"
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null || (event.inlineQuery.query isBlank)) return null
		
		List(
			InlineQueryUnit(InlineQueryResultArticle(
				inlineIds(ID_PREFIX), TITLE,
				new InputTextMessageContent(
					TelegramUserInformation informationOutputHTML event.inlineQuery.from
				).parseMode(ParseMode HTML)
			)).isPersonal(true).cacheTime(10)
		)
		
	}
	
}
