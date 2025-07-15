package cc.sukazyo.cono.morny.tele_utils.user_info

import cc.sukazyo.cono.morny.system.telegram_api.event.EventEnv
import cc.sukazyo.cono.morny.system.telegram_api.formatting.NamingUtils.inlineQueryId
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramUserInformation
import cc.sukazyo.cono.morny.system.telegram_api.inline_query.{QueryListener, QueryResultUnit}
import cc.sukazyo.cono.morny.util.SttpPublic
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent, ParseMode}

import scala.language.postfixOps

class InlineMyInformation extends QueryListener {
	
	private val ID_PREFIX = "[morny/info/me]"
	private val TITLE = "My Account Information"
	
	override def onQuery (query: InlineQuery)(using EventEnv): List[QueryResultUnit[?]] = {
		
		if !((query.query eq null) || (query.query isEmpty)) then return Nil
		
		List(
			QueryResultUnit(InlineQueryResultArticle(
				inlineQueryId(ID_PREFIX), TITLE,
				new InputTextMessageContent(
					TelegramUserInformation.getFormattedInformation(query.from)(using SttpPublic.mornyBasicRequest)
				).parseMode(ParseMode HTML)
			)).isPersonal(true).cacheTime(10)
		)
		
	}
	
}
