package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle

import scala.language.postfixOps
import scala.util.matching.Regex

class ShareToolTwitter extends ITelegramQuery {
	
	private val TITLE_VX = "[tweet] Share as VxTwitter"
	private val ID_PREFIX_VX = "[morny/share/twitter/vxtwi]"
	private val TITLE_FX = "[tweet] Share as Fix-Tweet"
	private val ID_PREFIX_FX = "[morny/share/twitter/fxtwi]"
	private val REGEX_TWEET_LINK: Regex = "^(?:https?://)?((?:(?:c\\.)?vx|fx|www\\.)?twitter|(?:www\\.|fixup)?x)\\.com/((\\w+)/status/(\\d+)(?:/photo/(\\d+))?)/?(\\?[\\w&=-]+)?$"r
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null) return null
		
		event.inlineQuery.query match
			
			case REGEX_TWEET_LINK(_, _path_data, _, _, _, _) =>
				List(
					InlineQueryUnit(InlineQueryResultArticle(
						inlineQueryId(ID_PREFIX_FX + event.inlineQuery.query), TITLE_FX,
						s"https://fxtwitter.com/$_path_data"
					)),
					InlineQueryUnit(InlineQueryResultArticle(
						inlineQueryId(ID_PREFIX_VX+event.inlineQuery.query), TITLE_VX,
						s"https://vxtwitter.com/$_path_data"
					))
				)
			
			case _ => null
		
	}
	
}
