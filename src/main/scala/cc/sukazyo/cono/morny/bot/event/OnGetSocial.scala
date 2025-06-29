package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.bot.event.OnGetSocial.tryFetchSocial
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.extra.{twitter, weibo, BilibiliForms}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.data.social.{SocialBilibiliParser, SocialTwitterParser, SocialWeiboParser}
import cc.sukazyo.cono.morny.extra.BilibiliForms.{BiliB23, BiliVideoId}
import cc.sukazyo.cono.morny.extra.bilibili.XWebAPI
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Message.entitiesSafe
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendPhoto, SendSticker}

class OnGetSocial (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update.message as messageEvent
		
		if messageEvent.chat.`type` != Chat.Type.Private then return;
		if messageEvent.text == null then return;
		
		if tryFetchSocial(
			Left((
					messageEvent.text :: messageEvent.entitiesSafe.map(f => f.url).filterNot(f => f == null)
			).mkString(" "))
		)(using messageEvent.chat.id, messageEvent.messageId) then
			event.setEventOk
		
	}
	
}

object OnGetSocial {
	
	/** Try fetch from url from input and output fetched social content.
	  *
	  * @param text input text, receive either a texts contains some URLs that should
	  *             pass through [[Left]], or a exactly URL that should pass through
	  *             [[Right]].
	  * @param replyChat chat that should be output to.
	  * @param replyToMessage message that should be reply to.
	  * @param coeur [[MornyCoeur]] instance for executing Telegram function.
	  * @return [[true]] if fetched social content and sent something out.
	  */
	def tryFetchSocial (text: Either[String, String])(using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur): Boolean = {
		
		var succeed = 0
		
		{
			text match
				case Left(texts) =>
					twitter.guessTweetUrl(texts.trim)
				case Right(url) =>
					twitter.parseTweetUrl(url.trim).toList
		}.map(f => {
			succeed += 1
			tryFetchSocialOfTweet(f)
		})
		
		{
			text match
				case Left(texts) =>
					weibo.guessWeiboStatusUrl(texts.trim)
				case Right(url) =>
					weibo.parseWeiboStatusUrl(url.trim).map(url -> _).toList
		}.map { (url, status) =>
			succeed += 1
			tryFetchSocialOfWeibo(status, url)
		}
		
		{
			val bilibiliVideos: List[BiliVideoId] = text match
				case Left(texts) =>
					BiliVideoId.searchIn(texts) ::: BiliB23.searchIn(texts).map(_.toVideoId)
				case Right(url) =>
					try BilibiliForms.parse_videoUrl(url) :: Nil
					catch case _: IllegalArgumentException =>
						try BiliVideoId.matchUrl(BilibiliForms.destructB23Url(url)) :: Nil
						catch case _: Exception => Nil
			bilibiliVideos.foreach( video =>
				succeed += 1
				tryFetchSocialOfBilibili(video)
			)
		}
		
		succeed > 0
		
	}
	
	private def tryFetchSocialOfBilibili (video: BiliVideoId)(using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur) = {
		import io.circe.{DecodingFailure, ParsingFailure}
		import sttp.client3.SttpClientException
		try {
			val video_info = XWebAPI.get_view(video)
			coeur.account exec new SendPhoto(
				replyChat,
				video_info.data.pic
			).replyToMessageId(replyToMessage)
				.caption(
					SocialBilibiliParser.printsBilibiliVideoCaption(video, video_info.data)
				).parseMode(ParseMode.HTML)
		} catch case e: (SttpClientException | ParsingFailure | DecodingFailure) =>
			coeur.account exec SendSticker(
				replyChat,
				TelegramStickers.ID_NETWORK_ERR
			).replyToMessageId(replyToMessage)
			logger error
				"Error on requesting Bilibili API\n" + exceptionLog(e)
			coeur.daemons.reporter.exception(e, "Error on requesting Bilibili API")
	}
	
	private def tryFetchSocialOfTweet (url: twitter.TweetUrlInformation)(using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur) =
		import io.circe.{DecodingFailure, ParsingFailure}
		import sttp.client3.SttpClientException
		import twitter.FXApi
		try {
			val api = FXApi.Fetch.status(Some(url.screenName), url.statusId)
			SocialTwitterParser.parseFXTweet(api).outputToTelegram
		} catch case e: (SttpClientException | ParsingFailure | DecodingFailure) =>
			coeur.account exec SendSticker(
				replyChat,
				TelegramStickers.ID_NETWORK_ERR
			).replyToMessageId(replyToMessage)
			logger error
				"Error on requesting FixTweet API\n" + exceptionLog(e)
			coeur.daemons.reporter.exception(e, "Error on requesting FixTweet API")
	
	private def tryFetchSocialOfWeibo (url: weibo.StatusUrlInfo, rawUrl: String)(using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur) =
		import io.circe.{DecodingFailure, ParsingFailure}
		import sttp.client3.{HttpError, SttpClientException}
		import weibo.MApi
		try {
			val api = MApi.Fetch.statuses_show(url.id)
			SocialWeiboParser.parseMStatus(api)(rawUrl).outputToTelegram
		} catch
			case e: HttpError[?] =>
				coeur.account exec SendMessage(
					replyChat,
					// language=html
					s"""Weibo Request Error <code>${e.statusCode}</code>
					   |<pre><code>${e.body}</code></pre>""".stripMargin
				).replyToMessageId(replyToMessage).parseMode(ParseMode.HTML)
			case e: (SttpClientException | ParsingFailure | DecodingFailure) =>
				coeur.account exec SendSticker(
					replyChat,
					TelegramStickers.ID_NETWORK_ERR
				).replyToMessageId(replyToMessage)
				logger error
					"Error on requesting Weibo m.API\n" + exceptionLog(e)
				coeur.daemons.reporter.exception(e, "Error on requesting Weibo m.API")
	
}
