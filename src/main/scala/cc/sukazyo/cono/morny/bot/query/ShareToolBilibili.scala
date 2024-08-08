package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent, ParseMode}

import scala.language.postfixOps

class ShareToolBilibili (using coeur: MornyCoeur) extends ITelegramQuery {
	
	private val TITLE_BILI_AV = "[bilibili] Share video / av"
	private val TITLE_BILI_BV = "[bilibili] Share video / BV"
	private val ID_PREFIX_BILI_AV = "[morny/share/bili/av]"
	private val ID_PREFIX_BILI_BV = "[morny/share/bili/bv]"
	private val SHARE_FORMAT_HTML = "<a href='%s'>%s</a>"
	private def formatShareHTML (url: String, name: String): String = SHARE_FORMAT_HTML.format(url, name)
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null) return null
		if (event.inlineQuery.query isBlank) return null
		
		import cc.sukazyo.cono.morny.extra.BilibiliForms.*
		val result: BiliVideoId =
			try
				parse_videoUrl(event.inlineQuery.query)
			catch case _: IllegalArgumentException =>
				try
					parse_videoUrl(destructB23Url(event.inlineQuery.query))
				catch
					case _: IllegalArgumentException =>
						return null
					case e: IllegalStateException =>
						logger error exceptionLog(e)
						coeur.daemons.reporter.exception(e)
						return null
		
		List(
			InlineQueryUnit(InlineQueryResultArticle(
				inlineQueryId(ID_PREFIX_BILI_AV + result.av), TITLE_BILI_AV + result.av,
				InputTextMessageContent(formatShareHTML(result.avLink, result.toAvString)).parseMode(ParseMode HTML)
			)),
			InlineQueryUnit(InlineQueryResultArticle(
				inlineQueryId(ID_PREFIX_BILI_BV + result.bv), TITLE_BILI_BV + result.bv,
				InputTextMessageContent(formatShareHTML(result.bvLink, result.toBvString)).parseMode(ParseMode HTML)
			))
		)
		
	}
	
}
