package cc.sukazyo.cono.morny.system.telegram_api.chat

trait ThreadableChat extends Chat {
	def threadId: Int
}
