package cc.sukazyo.cono.morny.social_share.query

import cc.sukazyo.cono.morny.social_share.external.twitter
import cc.sukazyo.cono.morny.social_share.external.twitter.TweetUrlInformation
import cc.sukazyo.cono.morny.system.telegram_api.event.EventEnv
import cc.sukazyo.cono.morny.system.telegram_api.formatting.NamingUtils.inlineQueryId
import cc.sukazyo.cono.morny.system.telegram_api.inline_query.{QueryListener, QueryResultUnit}
import com.pengrad.telegrambot.model.InlineQuery
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle

import scala.language.postfixOps

class ShareToolTwitter extends QueryListener {
	
	private val TITLE_VX = "[tweet] Share as VxTwitter"
	private val ID_PREFIX_VX = "[morny/share/twitter/vxtwi]"
	private val TITLE_FX = "[tweet] Share as Fix-Tweet"
	private val ID_PREFIX_FX = "[morny/share/twitter/fxtwi]"
	
	override def onQuery (query: InlineQuery)(using EventEnv): List[QueryResultUnit[?]] = {
		
		if (query.query == null) return Nil
		
		twitter.parseTweetUrl(query.query) match
			
			case Some(TweetUrlInformation(_, _path_data, _, _, _, _)) =>
				List(
					QueryResultUnit(InlineQueryResultArticle(
						inlineQueryId(ID_PREFIX_FX + query.query), TITLE_FX,
						s"https://fxtwitter.com/$_path_data"
					)),
					QueryResultUnit(InlineQueryResultArticle(
						inlineQueryId(ID_PREFIX_VX+query.query), TITLE_VX,
						s"https://vxtwitter.com/$_path_data"
					))
				)
			
			case _ => Nil
		
	}
	
}
