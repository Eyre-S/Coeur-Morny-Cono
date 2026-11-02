package cc.sukazyo.cono.morny.social_share.event

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.reporter.MornyReport
import cc.sukazyo.cono.morny.social_share.api.{SocialTwitterParser, SocialWeiboParser}
import cc.sukazyo.cono.morny.social_share.event.OnGetSocial.tryFetchSocial
import cc.sukazyo.cono.morny.social_share.external.{twitter, weibo}
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Message.textWithUrls
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import com.pengrad.telegrambot.model.Chat

class OnGetSocial (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update.message as messageEvent
		
		if messageEvent.chat.`type` != Chat.Type.Private then return;
		if messageEvent.text == null then return;
		
		if tryFetchSocial(
			Left(messageEvent.textWithUrls)
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
					weibo.parseWeiboStatusUrl(url.trim).toList
		}.map(f => {
			succeed += 1
			tryFetchSocialOfWeibo(f)
		})
		succeed > 0
		
	}
	
	def tryFetchSocialOfTweet (url: twitter.TweetUrlInformation)(using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur): Unit =
		import cc.sukazyo.cono.morny.social_share.external.twitter.FXApi
		import coeur.dsl.given
		import io.circe.{DecodingFailure, ParsingFailure}
		import sttp.client3.SttpClientException
		try {
			val api = FXApi.Fetch.status(Some(url.screenName), url.statusId)
			SocialTwitterParser.parseFXTweet(api).outputToTelegram
		} catch case e: (SttpClientException | ParsingFailure | DecodingFailure) =>
			// TODO: MessageThread support
			Messages.create(replyChat).replyTo(replyToMessage)
				.sticker(TelegramStickers.ID_NETWORK_ERR)
				.send
			logger `error`
				"Error on requesting FixTweet API\n" + e.toLogString
			coeur.externalContext.consume[MornyReport](_.exception(e, "Error on requesting FixTweet API"))
	
	def tryFetchSocialOfWeibo (url: weibo.StatusUrlInfo)(using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur): Unit =
		import cc.sukazyo.cono.morny.social_share.external.weibo.MApi
		import coeur.dsl.given
		import io.circe.{DecodingFailure, ParsingFailure}
		import sttp.client3.{HttpError, SttpClientException}
		val ccMsg = Messages.create(replyChat).replyTo(replyToMessage)
		try {
			val api = MApi.Fetch.statuses_show(url.id)
			SocialWeiboParser.parseMStatus(api).outputToTelegram
		} catch
			case e: HttpError[?] =>
				ccMsg(Texts.html(
					// language=html
					s"""Weibo Request Error <code>${e.statusCode}</code>
					   |<pre><code>${e.body}</code></pre>""".stripMargin
				)).send
			case e: (SttpClientException | ParsingFailure | DecodingFailure) =>
				ccMsg.sticker(TelegramStickers.ID_NETWORK_ERR).send
				logger `error`
					"Error on requesting Weibo m.API\n" + e.toLogString
				coeur.externalContext.consume[MornyReport](_.exception(e, "Error on requesting Weibo m.API"))
	
}
