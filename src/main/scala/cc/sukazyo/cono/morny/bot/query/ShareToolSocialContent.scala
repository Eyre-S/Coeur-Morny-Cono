package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.data.social.{SocialTwitterParser, SocialWeiboParser}
import cc.sukazyo.cono.morny.extra.{twitter, weibo}
import cc.sukazyo.cono.morny.extra.twitter.FXApi
import cc.sukazyo.cono.morny.extra.weibo.MApi
import com.pengrad.telegrambot.model.Update

class ShareToolSocialContent extends ITelegramQuery {
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		val query = event.inlineQuery.query
		if query == null then return null
		
		twitterTweets(query) ::: weiboStatus(query)
		
	}
	
	private def twitterTweets (query: String): List[InlineQueryUnit[_]] = {
		twitter.guessTweetUrl(query).flatMap { tweet =>
			SocialTwitterParser.parseFXTweet(FXApi.Fetch.status(Some(tweet.statusPath), tweet.statusId))
				.genInlineQueryResults(using
					"morny/share/tweet/content", tweet.statusId,
					"Twitter Tweet Content"
				)
		}
	}
	
	private def weiboStatus (query: String): List[InlineQueryUnit[_]] = {
		weibo.guessWeiboStatusUrl(query).flatMap { status =>
			SocialWeiboParser.parseMStatus(MApi.Fetch.statuses_show(status.id))
				.genInlineQueryResults(using
					"morny/share/weibo/status/content", status.id,
					"Weibo Content"
				)
		}
	}
	
}
