package cc.sukazyo.cono.morny.social_share.query

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{InlineQueryUnit, ITelegramQuery}
import cc.sukazyo.cono.morny.reporter.MornyReport
import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent, ParseMode}

import scala.language.postfixOps
import scala.util.matching.Regex

class ShareToolBilibili (using coeur: MornyCoeur) extends ITelegramQuery {
	
	private val TITLE_BILI_AV = "[bilibili] Share video / av"
	private val TITLE_BILI_BV = "[bilibili] Share video / BV"
	private val ID_PREFIX_BILI_AV = "[morny/share/bili/av]"
	private val ID_PREFIX_BILI_BV = "[morny/share/bili/bv]"
	private val LINK_PREFIX = "https://bilibili.com/video/"
	private val SHARE_FORMAT_HTML = "<a href='%s'>%s</a>"
	
	override def query (event: Update): List[InlineQueryUnit[?]] | Null = {
		
		if (event.inlineQuery.query == null) return null
		if (event.inlineQuery.query isBlank) return null
		
		import cc.sukazyo.cono.morny.social_share.external.bilibili.BilibiliForms.*
		val result: BiliVideoId =
			try
				parse_videoUrl(event.inlineQuery.query)
			catch case _: IllegalArgumentException =>
				try
					parse_videoUrl(destructB23Url(event.inlineQuery.query))
				catch
					case _: IllegalArgumentException =>
						return null;
					case e: IllegalStateException =>
						logger `error` e.toLogString
						coeur.externalContext.consume[MornyReport](_.exception(e))
						return null;
		
		val av = result.av
		val bv = result.bv
		val id_av = s"av$av"
		val id_bv = s"BV$bv"
		val linkParams = if (result.part != null) s"?p=${result.part}" else ""
		val link_av = LINK_PREFIX + id_av + linkParams
		val link_bv = LINK_PREFIX + id_bv + linkParams
		List(
			InlineQueryUnit(InlineQueryResultArticle(
				inlineQueryId(ID_PREFIX_BILI_AV + av), TITLE_BILI_AV + av,
				InputTextMessageContent(SHARE_FORMAT_HTML.format(link_av, id_av)).parseMode(ParseMode HTML)
			)),
			InlineQueryUnit(InlineQueryResultArticle(
				inlineQueryId(ID_PREFIX_BILI_BV + bv), TITLE_BILI_BV + bv,
				InputTextMessageContent(SHARE_FORMAT_HTML.format(link_bv, id_bv)).parseMode(ParseMode HTML)
			))
		)
		
	}
	
}
