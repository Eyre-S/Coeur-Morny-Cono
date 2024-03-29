package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramUserInformation
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent, ParseMode}

import scala.language.postfixOps

class MyInformation extends ITelegramQuery {
	
	private val ID_PREFIX = "[morny/info/me]"
	private val TITLE = "My Account Information"
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if !((event.inlineQuery.query eq null) || (event.inlineQuery.query isEmpty)) then return null
		
		List(
			InlineQueryUnit(InlineQueryResultArticle(
				inlineQueryId(ID_PREFIX), TITLE,
				new InputTextMessageContent(
					TelegramUserInformation getFormattedInformation event.inlineQuery.from
				).parseMode(ParseMode HTML)
			)).isPersonal(true).cacheTime(10)
		)
		
	}
	
}
