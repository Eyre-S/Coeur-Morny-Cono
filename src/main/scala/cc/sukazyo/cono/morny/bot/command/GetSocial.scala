package cc.sukazyo.cono.morny.bot.command
import cc.sukazyo.cono.morny.data.{twitter, weibo, TelegramStickers}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.twitter.{FXApi, TweetUrlInformation}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.data.weibo.StatusUrlInfo
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InputMedia, InputMediaPhoto, InputMediaVideo, ParseMode}
import com.pengrad.telegrambot.request.{SendMediaGroup, SendMessage, SendSticker}
import io.circe.{DecodingFailure, ParsingFailure}
import sttp.client3.{HttpError, SttpClientException}

class GetSocial (using coeur: MornyCoeur) extends ITelegramCommand {
	
	override val name: String = "get"
	override val aliases: Array[ICommandAlias] | Null = null
	override val paramRule: String = "<tweet-url|weibo-status-url>"
	override val description: String = "从社交媒体分享链接获取其内容"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		def do404 (): Unit =
			coeur.account exec SendSticker(
				event.message.chat.id,
				TelegramStickers.ID_404
			).replyToMessageId(event.message.messageId())
		
		if command.args.length < 1 then { do404(); return }
		
		var succeed = 0
		twitter.parseTweetUrl(command.args(0)) match
			case None =>
			case Some(TweetUrlInformation(_, _, screenName, statusId, _, _)) =>
				succeed += 1
				try {
					val api = FXApi.Fetch.status(Some(screenName), statusId)
					import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
					api.tweet match
						case None =>
							coeur.account exec SendMessage(
								event.message.chat.id,
								// language=html
								s"""❌ Fix-Tweet <code>${api.code}</code>
								   |<i>${h(api.message)}</i>""".stripMargin
							).replyToMessageId(event.message.messageId).parseMode(ParseMode.HTML)
						case Some(tweet) =>
							val content: String =
								// language=html
								s"""⚪️ <b>${h(tweet.author.name)} <a href="${tweet.author.url}">@${h(tweet.author.screen_name)}</a></b>
								   |
								   |${h(tweet.text)}
								   |
								   |<i>💬${tweet.replies}   🔗${tweet.retweets}   ❤️${tweet.likes}</i>
								   |<i><a href="${tweet.url}">${h(tweet.created_at)}</a></i>""".stripMargin
							tweet.media match
								case None =>
									coeur.account exec SendMessage(
										event.message.chat.id,
										content
									).replyToMessageId(event.message.messageId).parseMode(ParseMode.HTML)
								case Some(media) =>
									val mediaGroup: List[InputMedia[?]] =
										(
											media.photos match
												case None => List.empty
												case Some(photos) => for i <- photos yield InputMediaPhoto(i.url)
										) ::: (
											media.videos match
												case None => List.empty
												case Some(videos) => for i <- videos yield InputMediaVideo(i.url)
										)
									mediaGroup.head.caption(content)
									mediaGroup.head.parseMode(ParseMode.HTML)
									coeur.account exec SendMediaGroup(
										event.message.chat.id,
										mediaGroup:_*
									).replyToMessageId(event.message.messageId)
				} catch case e: (SttpClientException|ParsingFailure|DecodingFailure) =>
					coeur.account exec SendSticker(
						event.message.chat.id,
						TelegramStickers.ID_NETWORK_ERR
					).replyToMessageId(event.message.messageId)
					logger error
						"Error on requesting FixTweet API\n" + exceptionLog(e)
					coeur.daemons.reporter.exception(e, "Error on requesting FixTweet API")
		
		weibo.parseWeiboStatusUrl(command.args(0)) match
			case None =>
			case Some(StatusUrlInfo(_, id)) =>
				succeed += 1
				try {
					val api = weibo.MApi.Fetch.statuses_show(id)
					import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.{cleanupHtml as ch, escapeHtml as h}
					val content =
						// language=html
						s"""🔸<b><a href="${api.data.user.profile_url}">${h(api.data.user.screen_name)}</a></b>
						   |
						   |${ch(api.data.text)}
						   |
						   |<i><a href="${weibo.genWeiboStatusUrl(StatusUrlInfo(api.data.user.id.toString, api.data.id))}">${h(api.data.created_at)}</a></i>""".stripMargin
					api.data.pics match
						case None =>
							coeur.account exec SendMessage(
								event.message.chat.id,
								content
							).replyToMessageId(event.message.messageId).parseMode(ParseMode.HTML)
						case Some(pics) =>
//							val mediaGroup = pics.map(f =>
//								InputMediaPhoto(weibo.PicUrl(weibo.randomPicCdn, "large", f.pid).toUrl))
							val mediaGroup = pics.map(f => InputMediaPhoto(weibo.MApi.Fetch.pic(f.large.url)))
							mediaGroup.head.caption(content)
							mediaGroup.head.parseMode(ParseMode.HTML)
							coeur.account exec SendMediaGroup(
								event.message.chat.id,
								mediaGroup:_*
							).replyToMessageId(event.message.messageId)
				} catch
					case e: HttpError[?] =>
						coeur.account exec SendMessage(
							event.message.chat.id,
							// language=html
							s"""Weibo Request Error <code>${e.statusCode}</code>
							   |<pre><code>${e.body}</code></pre>""".stripMargin
						).replyToMessageId(event.message.messageId).parseMode(ParseMode.HTML)
					case e: (SttpClientException|ParsingFailure|DecodingFailure) =>
						coeur.account exec SendSticker(
							event.message.chat.id,
							TelegramStickers.ID_NETWORK_ERR
						).replyToMessageId(event.message.messageId)
						logger error
							"Error on requesting Weibo m.API\n" + exceptionLog(e)
						coeur.daemons.reporter.exception(e, "Error on requesting Weibo m.API")
		
		if succeed == 0 then do404()
		
	}
	
}
