package cc.sukazyo.cono.morny.bot.command
import cc.sukazyo.cono.morny.data.{twitter, TelegramStickers}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.twitter.{FXApi, TweetUrlInformation}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InputMedia, InputMediaPhoto, InputMediaVideo, ParseMode}
import com.pengrad.telegrambot.request.{SendMediaGroup, SendMessage, SendSticker}

class Tweet (using coeur: MornyCoeur) extends ITelegramCommand {
	
	override val name: String = "tweet"
	override val aliases: Array[ICommandAlias] | Null = null
	override val paramRule: String = "<tweet-url>"
	override val description: String = "获取 Twitter(X) Tweet 内容"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		def do404 (): Unit =
			coeur.account exec SendSticker(
				event.message.chat.id,
				TelegramStickers.ID_404
			).replyToMessageId(event.message.messageId())
		
		if command.args.length < 1 then { do404(); return }
		
		twitter.parseTweetUrl(command.args(0)) match
			case None => do404()
			case Some(TweetUrlInformation(_, _, screenName, statusId, _, _)) =>
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
				} catch case e: Exception =>
					coeur.account exec SendSticker(
						event.message.chat.id,
						TelegramStickers.ID_NETWORK_ERR
					).replyToMessageId(event.message.messageId)
					logger attention
						"Error on requesting FixTweet API\n" + exceptionLog(e)
					coeur.daemons.reporter.exception(e, "Error on requesting FixTweet API")
		
	}
	
}
