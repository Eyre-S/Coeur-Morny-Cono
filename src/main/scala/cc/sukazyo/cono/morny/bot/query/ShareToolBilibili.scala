package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import cc.sukazyo.cono.morny.util.BiliTool
import cc.sukazyo.cono.morny.util.UseSelect.select
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent, ParseMode}

import scala.language.postfixOps
import scala.util.matching.Regex

class ShareToolBilibili extends ITelegramQuery {
	
	private val TITLE_BILI_AV = "[bilibili] Share video / av"
	private val TITLE_BILI_BV = "[bilibili] Share video / BV"
	private val ID_PREFIX_BILI_AV = "[morny/share/bili/av]"
	private val ID_PREFIX_BILI_BV = "[morny/share/bili/bv]"
	private val LINK_PREFIX = "https://bilibili.com/video/"
	private val REGEX_BILI_VIDEO: Regex = "^(?:(?:https?://)?(?:www\\.)?bilibili\\.com(?:/s)?/video/((?:av|AV)(\\d{1,12})|(?:bv|BV)([A-HJ-NP-Za-km-z1-9]{10}))/?(\\?(?:p=(\\d+))?.*)?|(?:av|AV)(\\d{1,12})|(?:bv|BV)([A-HJ-NP-Za-km-z1-9]{10}))$"r
	private val SHARE_FORMAT_HTML = "<a href='%s'>%s</a>"
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null) return null
		
		event.inlineQuery.query match
			case REGEX_BILI_VIDEO(_url_v, _url_av, _url_bv, _url_param, _url_v_part, _raw_av, _raw_bv) =>
				
				logger debug
						s"""====== Share Tool Bilibili Catch ok
						   |1: ${_url_v}
						   |2: ${_url_av}
						   |3: ${_url_bv}
						   |4: ${_url_param}
						   |5: ${_url_v_part}
						   |6: ${_raw_av}
						   |7: ${_raw_bv}"""
						.stripMargin
				
				var av = select(_url_av, _raw_av)
				var bv = select(_url_bv, _raw_bv)
				logger trace s"catch id av[$av] bv[$bv]"
				val part: Int|Null = if (_url_v_part!=null) _url_v_part toInt else null
				logger trace s"catch video part[$part]"
				
				if (av == null) {
					assert (bv != null)
					av = BiliTool.toAv(bv) toString;
					logger trace s"converted bv[$av] to av[$av]"
				} else {
					bv = BiliTool.toBv(av toLong)
					logger trace s"converted av[$av] to bv[$bv]"
				}
				
				val id_av = s"av$av"
				val id_bv = s"BV$bv"
				val linkParams = if (part!=null) s"?p=$part" else ""
				val link_av = LINK_PREFIX + id_av + linkParams
				val link_bv = LINK_PREFIX + id_bv + linkParams
				
				List(
					InlineQueryUnit(InlineQueryResultArticle(
						inlineQueryId(ID_PREFIX_BILI_AV+av), TITLE_BILI_AV+av,
						InputTextMessageContent(SHARE_FORMAT_HTML.format(link_av, id_av)).parseMode(ParseMode HTML)
					)),
					InlineQueryUnit(InlineQueryResultArticle(
						inlineQueryId(ID_PREFIX_BILI_BV + bv), TITLE_BILI_BV + bv,
						InputTextMessageContent(SHARE_FORMAT_HTML.format(link_bv, id_bv)).parseMode(ParseMode HTML)
					))
				)
				
			case _ => null
		
	}
	
}
