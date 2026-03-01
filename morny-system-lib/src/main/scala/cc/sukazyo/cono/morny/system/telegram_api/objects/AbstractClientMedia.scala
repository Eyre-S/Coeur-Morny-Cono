package cc.sukazyo.cono.morny.system.telegram_api.objects

import cc.sukazyo.cono.morny.system.telegram_api.text.MessageText
import com.pengrad.telegrambot.model.request.InputMedia

trait AbstractClientMedia [T <: InputMedia[T]] {
	
	def mediaType: String
	def mediaData: ClientMediaData
	
	def caption: Option[MessageText]
	
	// todo: implement this
	def toNative: InputMedia[T]
	
	def decorateNative [T2 <: InputMedia[T2]] (native: T2): Unit = {
		this.caption.map { it =>
			val text = it.compile
			native.caption(text.message)
			text.parseMode.map(native.parseMode)
			if text.entities.nonEmpty then
				native.captionEntities(text.entities *)
		}
	}
	
}
