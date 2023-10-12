package cc.sukazyo.cono.morny.bot.api

trait EventListener () {
	
	def onMessage (using EventEnv): Unit = {}
	def onEditedMessage (using EventEnv): Unit = {}
	def onChannelPost (using EventEnv): Unit = {}
	def onEditedChannelPost (using EventEnv): Unit = {}
	def onInlineQuery (using EventEnv): Unit = {}
	def onChosenInlineResult (using EventEnv): Unit = {}
	def onCallbackQuery (using EventEnv): Unit = {}
	def onShippingQuery (using EventEnv): Unit = {}
	def onPreCheckoutQuery (using EventEnv): Unit = {}
	def onPoll (using EventEnv): Unit = {}
	def onPollAnswer (using EventEnv): Unit = {}
	def onMyChatMemberUpdated (using EventEnv): Unit = {}
	def onChatMemberUpdated (using EventEnv): Unit = {}
	def onChatJoinRequest (using EventEnv): Unit = {}
	
}
