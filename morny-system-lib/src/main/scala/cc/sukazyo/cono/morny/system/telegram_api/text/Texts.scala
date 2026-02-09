package cc.sukazyo.cono.morny.system.telegram_api.text

object Texts {
	
	def plain (message: String): NativeText =
		NativeText.plain(message)
	def html (message: String): NativeText =
		NativeText.html(message)
	def markdownV2 (message: String): NativeText =
		NativeText.markdownV2(message)
	def markdown (message: String): NativeText =
		NativeText.markdown(message)
	
}
