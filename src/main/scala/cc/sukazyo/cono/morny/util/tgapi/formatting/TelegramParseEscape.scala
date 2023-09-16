package cc.sukazyo.cono.morny.util.tgapi.formatting

object TelegramParseEscape {
	
	def escapeHtml (input: String): String =
		var process = input
		process = process.replaceAll("&", "&amp;")
		process = process.replaceAll("<", "&lt;")
		process = process.replaceAll(">", "&gt;")
		process
	
}
