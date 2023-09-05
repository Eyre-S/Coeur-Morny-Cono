package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.bot.api.InlineQueryUnit
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle

import cc.sukazyo.cono.morny.util.tgapi.formatting.NamedUtils.inlineIds

import scala.language.postfixOps
import scala.util.matching.Regex

object ShareToolTwitter extends ITelegramQuery {
	
	val TITLE_VX = "[tweet] Share as VxTwitter"
	val TITLE_VX_COMBINED = "[tweet] Share as VxTwitter(combination)"
	val ID_PREFIX_VX = "[morny/share/twitter/vxtwi]"
	val ID_PREFIX_VX_COMBINED = "[morny/share/twitter/vxtwi_combine]"
	val REGEX_TWEET_LINK: Regex = "^(?:https?://)?((?:(?:c\\.)?vx|fx|www\\.)?twitter\\.com)/((\\w+)/status/(\\d+)(?:/photo/(\\d+))?)/?(\\?[\\w&=-]+)?$"r
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null) return null
		
		event.inlineQuery.query match
			
			case REGEX_TWEET_LINK(_1, _2, _) =>
				List(
					InlineQueryUnit(InlineQueryResultArticle(
						inlineIds(ID_PREFIX_VX+event.inlineQuery.query), TITLE_VX,
						s"https://vxtwitter.com/$_2"
					)),
					InlineQueryUnit(InlineQueryResultArticle(
						inlineIds(ID_PREFIX_VX_COMBINED+event.inlineQuery.query), TITLE_VX_COMBINED,
						s"https://c.vxtwitter.com/$_2"
					))
				)
			
			case _ => null
		
	}
	
}
