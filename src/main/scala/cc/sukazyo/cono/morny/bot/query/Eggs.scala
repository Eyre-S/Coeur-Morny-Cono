package cc.sukazyo.cono.morny.bot.query

import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.formatting.NamingUtils.inlineQueryId
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.InlineQueryResultCachedSticker

class Eggs extends ITelegramQuery {
	
	private val ID_PREFIX = "[morny/info/me]"
	
	private def setSticker (stickerId: String): List[InlineQueryUnit[_]] = {
		InlineQueryUnit(InlineQueryResultCachedSticker(
			inlineQueryId(ID_PREFIX, stickerId),
			stickerId
		)) :: Nil
	}
	
	override def query (event: Update): List[InlineQueryUnit[_]] | Null = {
		val query = event.inlineQuery.query
		
		query match {
			case "install"  => setSticker(TelegramStickers.ID_PROGYNOVA)
			case "drop" => setSticker(TelegramStickers.ID_DROP_APPLE)
			case _ => null
		}
		
	}
	
}
