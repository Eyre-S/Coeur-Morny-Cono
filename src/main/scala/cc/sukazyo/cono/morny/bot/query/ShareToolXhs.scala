package cc.sukazyo.cono.morny.bot.query
import cc.sukazyo.cono.morny.extra.xhs.XHSLink
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle

class ShareToolXhs extends ITelegramQuery {
	
	private val TITLE = "[Xiaohongshu] Note"
	private val ID = "[morny/share/xhs/link]"
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		import event.inlineQuery
		
		if inlineQuery.query == null then return null
		val content = inlineQuery.query
		
		def getTitle (xhsLink: XHSLink): String = {
			s"$TITLE ${xhsLink.exploreId}"
		}
		
		val xhsLinks: List[(String, XHSLink, Option[String])] = {
			XHSLink.searchUrls(content).map {
				case xhsLink: XHSLink => (xhsLink.toString, xhsLink, None)
				case shareLink: XHSLink.ShareLink =>
					(shareLink.toString, shareLink.getXhsLink, Some(shareLink.link))
			}
		}
		
		xhsLinks.map((uniqueId, xhsLink, maybeFromShare) =>
			InlineQueryUnit(InlineQueryResultArticle(
				ID+uniqueId,
				getTitle(xhsLink),
				xhsLink.link
			).description(
				"URL only." + (if maybeFromShare.nonEmpty then s" from ${maybeFromShare.get}" else "")
			))
		)
		
	}
	
}
