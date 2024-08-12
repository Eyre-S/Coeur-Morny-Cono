package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.{InlineQueryResultArticle, InputTextMessageContent, ParseMode}

import scala.language.postfixOps

class ShareToolBilibili extends ITelegramQuery {
	
	private val TITLE_BILI_AV = "[bilibili] Share video / av"
	private val TITLE_BILI_BV = "[bilibili] Share video / BV"
	private val ID_PREFIX_BILI_AV = "[morny/share/bili/av]"
	private val ID_PREFIX_BILI_BV = "[morny/share/bili/bv]"
	private val SHARE_FORMAT_HTML = "<a href='%s'>%s</a>"
	private def formatShareHTML (url: String, name: String): String = SHARE_FORMAT_HTML.format(url, name)
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		
		if (event.inlineQuery.query == null) return null
		if (event.inlineQuery.query isBlank) return null
		val content = event.inlineQuery.query
		
		import cc.sukazyo.cono.morny.extra.BilibiliForms.*
		val results: List[(String, BiliVideoId)] =
			BiliVideoId.searchIn(content).map(x => (x.toString, x)) ++
			BiliB23.searchIn(content).map(x => (x.toString, x.toVideoId))
		
		results.flatMap( (_, it) =>
			List(
				InlineQueryUnit(InlineQueryResultArticle(
					inlineQueryId(ID_PREFIX_BILI_AV + it.av),
					TITLE_BILI_AV + it.av,
					InputTextMessageContent(formatShareHTML(it.avLink, it.toAvString)).parseMode(ParseMode HTML)
				)),
				InlineQueryUnit(InlineQueryResultArticle(
					inlineQueryId(ID_PREFIX_BILI_BV + it.bv),
					TITLE_BILI_BV + it.bv,
					InputTextMessageContent(formatShareHTML(it.bvLink, it.toBvString)).parseMode(ParseMode HTML)
				))
			)
		)
		
	}
	
}
