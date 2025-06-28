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
	
	override def onChatJoinRequest (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Some(event.update.chatJoinRequest.from),
			chat = Some(event.update.chatJoinRequest.chat),
			timestamp = Some(event.update.chatJoinRequest.date)
		)
	
	override def onChatMemberUpdated (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Some(event.update.chatMember.from),
			chat = Some(event.update.chatMember.chat),
			timestamp = Some(event.update.chatMember.date)
		)
	
	override def onInlineQuery (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Some(event.update.inlineQuery.from),
			textInput = Some(event.update.inlineQuery.query)
		)
	
	override def onChosenInlineResult (using event: EventEnv): Unit =
		event provide EventContext(
			invoker = Some(event.update.chosenInlineResult.from)
		)
	
}
