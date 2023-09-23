package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle

import scala.language.postfixOps
import scala.util.matching.Regex

class ShareToolTwitter extends ITelegramQuery {
	
	private val TITLE_VX = "[tweet] Share as VxTwitter"
	private val TITLE_VX_COMBINED = "[tweet] Share as VxTwitter(combination)"
	private val ID_PREFIX_VX = "[morny/share/twitter/vxtwi]"
	private val ID_PREFIX_VX_COMBINED = "[morny/share/twitter/vxtwi_combine]"
	private val REGEX_TWEET_LINK: Regex = "^(?:https?://)?((?:(?:c\\.)?vx|fx|www\\.)?twitter\\.com)/((\\w+)/status/(\\d+)(?:/photo/(\\d+))?)/?(\\?[\\w&=-]+)?$"r
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null) return null
		
		event.inlineQuery.query match
			
			case REGEX_TWEET_LINK(_, _2, _, _, _, _) =>
				List(
					InlineQueryUnit(InlineQueryResultArticle(
						inlineQueryId(ID_PREFIX_VX+event.inlineQuery.query), TITLE_VX,
						s"https://vxtwitter.com/$_2"
					)),
					InlineQueryUnit(InlineQueryResultArticle(
						inlineQueryId(ID_PREFIX_VX_COMBINED+event.inlineQuery.query), TITLE_VX_COMBINED,
						s"https://c.vxtwitter.com/$_2"
					))
				)
			
			case _ => null
		
	}
	
}
