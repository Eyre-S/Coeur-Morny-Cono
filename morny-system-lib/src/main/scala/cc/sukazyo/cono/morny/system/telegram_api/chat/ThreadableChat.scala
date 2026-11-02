package cc.sukazyo.cono.morny.system.telegram_api.chat

import cc.sukazyo.cono.morny.system.telegram_api.Standardize.MessageThreadID

trait ThreadableChat extends Chat {
	def threadId: MessageThreadID
}
