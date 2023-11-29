package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.bot.event.OnGetSocial.tryFetchSocial
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.extra.{twitter, weibo}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.data.social.{SocialTwitterParser, SocialWeiboParser}
import com.pengrad.telegrambot.model.Chat
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendSticker}

class OnGetSocial (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update.message as messageEvent
		
		if messageEvent.chat.`type` != Chat.Type.Private then return;
		if messageEvent.text == null then return;
		
		if tryFetchSocial(messageEvent.text)(using messageEvent.chat.id, messageEvent.messageId) then
			event.setEventOk
		
	}
	
}

object OnGetSocial {
	
	/** Try fetch from url from input and output fetched social content.
	  *
	  * @param text input text, maybe a social url.
	  * @param replyChat chat that should be output to.
	  * @param replyToMessage message that should be reply to.
	  * @param coeur [[MornyCoeur]] instance for executing Telegram function.
	  * @return [[true]] if fetched social content and sent something out.
	  */
	def tryFetchSocial (text: String)(using replyChat: Long, replyToMessage: Int)(using coeur: MornyCoeur): Boolean = {
		val _text = text.trim
		
		var succeed = 0
		
		import io.circe.{DecodingFailure, ParsingFailure}
		import sttp.client3.{HttpError, SttpClientException}
		import twitter.{FXApi, TweetUrlInformation}
		import weibo.{MApi, StatusUrlInfo}
		twitter.parseTweetUrl(_text) match
			case None =>
			case Some(TweetUrlInformation(_, _, screenName, statusId, _, _)) =>
				succeed += 1
				try {
					val api = FXApi.Fetch.status(Some(screenName), statusId)
					SocialTwitterParser.parseFXTweet(api).outputToTelegram
				} catch case e: (SttpClientException | ParsingFailure | DecodingFailure) =>
					coeur.account exec SendSticker(
						replyChat,
						TelegramStickers.ID_NETWORK_ERR
					).replyToMessageId(replyToMessage)
					logger error
						"Error on requesting FixTweet API\n" + exceptionLog(e)
					coeur.daemons.reporter.exception(e, "Error on requesting FixTweet API")
		
		weibo.parseWeiboStatusUrl(_text) match
			case None =>
			case Some(StatusUrlInfo(_, id)) =>
				succeed += 1
				try {
					val api = MApi.Fetch.statuses_show(id)
					SocialWeiboParser.parseMStatus(api).outputToTelegram
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
		
		succeed > 0
		
	}
	
}
