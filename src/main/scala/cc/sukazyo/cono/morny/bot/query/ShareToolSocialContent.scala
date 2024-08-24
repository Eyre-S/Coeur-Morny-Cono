package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.data.social.{SocialTwitterParser, SocialWeiboParser}
import cc.sukazyo.cono.morny.extra.{twitter, weibo}
import cc.sukazyo.cono.morny.extra.twitter.FXApi
import cc.sukazyo.cono.morny.extra.weibo.MApi
import cc.sukazyo.cono.morny.extra.BilibiliForms.{BiliB23, BiliVideoId}
import cc.sukazyo.cono.morny.extra.bilibili.XWebAPI
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultPhoto, ParseMode}

class ShareToolSocialContent extends ITelegramQuery {
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		val query = event.inlineQuery.query
		if query == null then return null
		
		twitterTweets(query) ::: weiboStatus(query) ::: bilibiliVideos(query)
		
	}
	
	private def bilibiliVideos (query: String): List[InlineQueryUnit[_]] = {
		
		val results: List[(String, BiliVideoId)] =
			BiliVideoId.searchIn(query).map(x => (x.toString, x)) ++
				BiliB23.searchIn(query).map(x => (x.toString, x.toVideoId))
		
		results.map { (_, video) =>
			val video_info = XWebAPI.get_view(video)
			InlineQueryUnit(InlineQueryResultPhoto(
				"[morny/share/bilibili/video/preview]" + video.av + "/" + video.bv,
				video_info.data.pic,
				video_info.data.pic
			).title(
				s"[Bilibili] ${video_info.data.title}"
			).description(
				s"av${video.av} / BV${video.bv} - Preview"
			).caption(
				// language=html
				s"""<a href="https://www.bilibili.com/video/av${video.av}"><b>${h(video_info.data.title)}</b></a>
				   |  <a href="https://space.bilibili.com/${video_info.data.owner.mid}">@${h(video_info.data.owner.name)}</a>
				   |${h(video_info.data.desc)}""".stripMargin
			).parseMode(ParseMode.HTML))
		}
		
	}
	
	private def twitterTweets (query: String): List[InlineQueryUnit[_]] = {
		twitter.guessTweetUrl(query).flatMap { tweet =>
			SocialTwitterParser.parseFXTweet(FXApi.Fetch.status(Some(tweet.statusPath), tweet.statusId))
				.genInlineQueryResults(using
					"morny/share/tweet/content", tweet.statusId,
					"[Twitter]"
				)
		}
	}
	
	private def weiboStatus (query: String): List[InlineQueryUnit[_]] = {
		weibo.guessWeiboStatusUrl(query).flatMap { (url, status) =>
			SocialWeiboParser.parseMStatus(MApi.Fetch.statuses_show(status.id))(url)
				.genInlineQueryResults(using
					"morny/share/weibo/status/content", status.id,
					"[Weibo]"
				)
		}
	}
	
}
