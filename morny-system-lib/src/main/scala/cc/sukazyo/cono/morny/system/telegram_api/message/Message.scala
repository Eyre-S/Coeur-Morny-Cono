package cc.sukazyo.cono.morny.system.telegram_api.message

trait Message (
	
	val effectID: Option[String],
	
	val replyMarkup: Null
	
) {
	
	def inlineKeyboard: this.replyMarkup.type = replyMarkup
	
}
