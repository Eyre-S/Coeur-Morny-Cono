package cc.sukazyo.cono.morny.system.telegram_api.event

trait EventListener () {
	
	/** Determine if this event listener should be processed.
	  *
	  * Default implementation is it only be `true` when the event
	  * is not ok yet (when [[EventEnv.isEventOk]] is false).
	  *
	  * Notice that: You should not override this method to filter some
	  * affair level conditions (such as if this update contains a text
	  * message), you should write them to the listener function! This
	  * method is just for event low-level controls.
	  *
	  * @param env The [[EventEnv event variable]].
	  * @return `true` if this event listener should run; `false`
	  *         if it should not run.
	  */
	def executeFilter (using env: EventEnv): Boolean =
		if env.state == null then true else false
	
	/** Run at all event listeners' listen methods done.
	  *
	  * Listen methods is the methods defined in [[EventListener this]]
	  * trait starts with `on`.
	  *
	  * This method will always run no matter the result of [[executeFilter]]
	  */
	def atEventPost (using EventEnv): Unit = {}
	
	/** A overall event listener that can listen every types that supported
	  * by the bot API.
	  *
	  * This method will runs before the specific event listener methods.
	  * 
	  * [[executeFilter]] will affect this method.
	  * 
	  * @since 2.0.0
	  */
	def on (using EventEnv): Unit = {}
	
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
