package cc.sukazyo.cono.morny.bot.api

trait EventListener () {
	
	/** Determine if this event listener should be processed.
	  *
	  * Default implementation is it only be [[true]] when the event
	  * is not ok yet (when [[EventEnv.isEventOk]] is false).
	  *
	  * Notice that: You should not override this method to filter some
	  * affair level conditions (such as if this update contains a text
	  * message), you should write them to the listener function! This
	  * method is just for event low-level controls.
	  *
	  * @param env The [[EventEnv event variable]].
	  * @return [[true]] if this event listener should run; [[false]]
	  *         if it should not run.
	  */
	def executeFilter (using env: EventEnv): Boolean =
		if env.isEventOk then false else true
	
	def atEventPost (using EventEnv): Unit = {}
	
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
