package cc.sukazyo.cono.morny.bot.query
import cc.sukazyo.cono.morny.data.social.{SocialTwitterParser, SocialWeiboParser}
import cc.sukazyo.cono.morny.extra.{twitter, weibo}
import cc.sukazyo.cono.morny.extra.twitter.{FXApi, TweetUrlInformation}
import cc.sukazyo.cono.morny.extra.weibo.{MApi, StatusUrlInfo}
import com.pengrad.telegrambot.model.Update

class ShareToolSocialContent extends ITelegramQuery {
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		val _queryRaw = event.inlineQuery.query
		val query =
			_queryRaw.trim match
				case _startsWithTag if _startsWithTag startsWith "get " =>
					(_startsWithTag drop 4)trim
				case _endsWithTag if _endsWithTag endsWith " get" =>
					(_endsWithTag dropRight 4)trim
				case _ => return null
		
		(
			twitter.parseTweetUrl(query) match
				case Some(TweetUrlInformation(_, statusPath, _, statusId, _, _)) =>
					SocialTwitterParser.parseFXTweet(FXApi.Fetch.status(Some(statusPath), statusId))
						.genInlineQueryResults(using
							"morny/share/tweet/content", statusId,
							"Twitter Tweet Content"
						)
				case None => Nil
		) ::: (
			weibo.parseWeiboStatusUrl(query) match
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
