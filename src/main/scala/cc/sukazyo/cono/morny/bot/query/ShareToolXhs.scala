package cc.sukazyo.cono.morny.bot.query
import cc.sukazyo.cono.morny.extra.xhs.XHSLink
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResultArticle

class ShareToolXhs extends ITelegramQuery {
	
	private val TITLE = "[Xiaohongshu] Share Link"
	private val ID = "[morny/share/xhs/link]"
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		import event.inlineQuery
		
		if inlineQuery.query == null then return null
		val content = inlineQuery.query
		
		val xhsLink: XHSLink = {
			XHSLink.matchUrl(content) match
				case Some(matched) => matched match
					case xhsLink: XHSLink => xhsLink
					case shareLink: XHSLink.ShareLink =>
						shareLink.getXhsLink
				case None =>
					XHSLink.searchShareText(content).map(_.getXhsLink) match
						case Some(found) => found
						case None => return null
		}
		
		List(
			InlineQueryUnit(InlineQueryResultArticle(
				ID+content.hashCode,
				TITLE,
				xhsLink.link
			))
		)
		
	}
	
}
