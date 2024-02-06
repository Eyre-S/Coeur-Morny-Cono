package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}

/** This will inject a [[EventContext]] for all the event types.
  * 
  * It will fill the [[EventContext]]'s field if possible. The context will be useful for event filtering, or error
  * report.
  * 
  * It is still in early development so some of the event types' context extractor may miss some fields.
  */
class MornyInjectEventContext extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit =
		event provide EventContext.fromMessage(event.update.message)
	override def onEditedMessage (using event: EventEnv): Unit =
		event provide EventContext.fromMessage(event.update.editedMessage)
	override def onChannelPost (using event: EventEnv): Unit =
		event provide EventContext.fromMessage(event.update.channelPost)
	override def onEditedChannelPost (using event: EventEnv): Unit =
		event provide EventContext.fromMessage(event.update.editedChannelPost)
	
	override def onInlineQuery (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Some(event.update.inlineQuery.from),
			textInput = Some(event.update.inlineQuery.query)
		)
	override def onChosenInlineResult (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Some(event.update.chosenInlineResult.from)
		)
	
	override def onCallbackQuery (using event: EventEnv): Unit =
		event provide EventContext(
			chat = Option(event.update.callbackQuery.message).map(_.chat),
			invoker = Some(event.update.callbackQuery.from)
		)
	override def onShippingQuery (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Some(event.update.shippingQuery.from)
		)
	override def onPreCheckoutQuery (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Option(event.update.preCheckoutQuery.from)
		)
	
	override def onPoll (using event: EventEnv): Unit =
		event provide EventContext(
			textInput = Some(event.update.poll.question)
		)
	
	override def onPollAnswer (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Option(event.update.pollAnswer.user),
		)
	
	override def onMyChatMemberUpdated (using event: EventEnv): Unit =
		event provide EventContext.fromChatMemberUpdated(event.update.myChatMember)
	override def onChatMemberUpdated (using event: EventEnv): Unit =
		event provide EventContext.fromChatMemberUpdated(event.update.chatMember)
	override def onChatJoinRequest (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Some(event.update.chatJoinRequest.from),
			chat = Some(event.update.chatJoinRequest.chat),
			timestamp = Some(event.update.chatJoinRequest.date)
		)
	
}
