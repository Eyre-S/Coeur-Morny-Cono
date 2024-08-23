package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.extra.twitter
import cc.sukazyo.cono.morny.extra.twitter.TweetUrlInformation
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle

import scala.language.postfixOps

class ShareToolTwitter extends ITelegramQuery {
	
	private val TITLE_VX = "[Twitter/X][VxTwitter]"
	private val ID_PREFIX_VX = "[morny/share/twitter/vx_url]"
	private val TITLE_FX = "[Twitter/X][Fix-Tweet]"
	private val ID_PREFIX_FX = "[morny/share/twitter/fx_url]"
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null) return null
		
		def getQueryTweetId (prefix: String, tweet: TweetUrlInformation): String =
			prefix + tweet.hashCode
		def getTweetName (title_prefix: String, tweet: TweetUrlInformation): String =
			s"$title_prefix ${tweet.screenName}/${tweet.statusId}"
		
		twitter.guessTweetUrl(event.inlineQuery.query).flatMap(tweet =>
			List(
				InlineQueryUnit(InlineQueryResultArticle(
					getQueryTweetId(ID_PREFIX_FX, tweet),
					getTweetName(TITLE_FX, tweet),
					s"https://fxtwitter.com/${tweet.statusPath}"
				)),
				InlineQueryUnit(InlineQueryResultArticle(
					getQueryTweetId(ID_PREFIX_VX, tweet),
					getTweetName(TITLE_VX, tweet),
					s"https://vxtwitter.com/${tweet.statusPath}"
				))
			)
		)
	}
	
}
