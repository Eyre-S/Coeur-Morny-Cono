package cc.sukazyo.cono.morny.social_share.query

import cc.sukazyo.cono.morny.social_share.api.{SocialTwitterParser, SocialWeiboParser}
import cc.sukazyo.cono.morny.social_share.external.{twitter, weibo}
import cc.sukazyo.cono.morny.social_share.external.twitter.{FXApi, TweetUrlInformation}
import cc.sukazyo.cono.morny.social_share.external.weibo.{MApi, StatusUrlInfo}
import cc.sukazyo.cono.morny.system.telegram_api.event.EventEnv
import cc.sukazyo.cono.morny.system.telegram_api.inline_query.{QueryListener, QueryResultUnit}
import com.pengrad.telegrambot.model.InlineQuery

class ShareToolSocialContent extends QueryListener {
	
	override def onQuery (query: InlineQuery)(using EventEnv): List[QueryResultUnit[?]] = {
		
		val _queryRaw = query.query
		val queryMsg =
			_queryRaw.trim match
				case _startsWithTag if _startsWithTag `startsWith` "get " =>
					(_startsWithTag drop 4)trim
				case _endsWithTag if _endsWithTag `endsWith` " get" =>
					(_endsWithTag dropRight 4)trim
				case _ => return Nil
		
		(
			twitter.parseTweetUrl(queryMsg) match
				case Some(TweetUrlInformation(_, statusPath, _, statusId, _, _)) =>
					SocialTwitterParser.parseFXTweet(FXApi.Fetch.status(Some(statusPath), statusId))
						.genInlineQueryResults(using
							"morny/share/tweet/content", statusId,
							"Twitter Tweet Content"
						)
				case None => Nil
		) ::: (
			weibo.parseWeiboStatusUrl(queryMsg) match
				case Some(StatusUrlInfo(_, id)) =>
					SocialWeiboParser.parseMStatus(MApi.Fetch.statuses_show(id))
						.genInlineQueryResults(using
							"morny/share/weibo/status/content", id,
							"Weibo Content"
						)
				case None => Nil
		) ::: Nil
		
	}
	
}
