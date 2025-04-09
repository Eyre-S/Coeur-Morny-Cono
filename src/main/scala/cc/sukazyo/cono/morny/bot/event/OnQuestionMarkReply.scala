package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.event.OnQuestionMarkReply.isAllMessageMark
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.Log.logger
import com.pengrad.telegrambot.request.{ForwardMessage, SendMessage}

import scala.language.postfixOps
import scala.util.boundary

class OnQuestionMarkReply (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update
		
		// FIXME: not tested yet, due to cannot connect to test bot
		if event.defined[MornyOnUpdateTimestampOffsetLock.ExpiredEvent.type] then {
			logger.debug("OnQuestionMarkReply: expired event, skipped")
			return
		} else {
			logger.debug("OnQuestionMarkReply: event continue")
		}
		
		if update.message.text eq null then return
		
		import cc.sukazyo.cono.morny.util.UseMath.over
		import cc.sukazyo.cono.morny.util.UseRandom.chance_is
		if !isAllMessageMark(using update.message.text) then return;
		// provide a mark to notify the following event
		//   that the current event is all made of question marks
		event.provide(OnQuestionMarkReply.Marked)
		
		if (1 over 8) chance_is false then return;
		
		if (update.message.hasProtectedContent) {
			// Copy the message
			// if the message cannot be forwarded.
			// Some chats may have restrictions on forwarding messages caused
			// we cannot use the best behavior (on the 'else' branch).
			// so use this as a fallback.
			// Due to the CopyMessage function only exists in the newer version's API,
			// we can only send the message again.
			// TODO: change to CopyMessage when the API is updated.
			// This is the old behavior with changes that removes the reply to the original message.
			coeur.account exec SendMessage(
				update.message.chat.id, update.message.text
			)
		} else {
			// forward the message
			//  if the message triggers question mark repeat
			coeur.account exec ForwardMessage(
				update.message.chat.id,
				update.message.chat.id, update.message.messageId
			)
		}
		event.setEventOk
		
	}
	
}

object OnQuestionMarkReply {
	
	// todo: due to the limitation of Java char, the ⁉️ character (actually not a
	//  single character) is not supported yet.
	private val QUESTION_MARKS = Set('?', '？', '¿', '⁈', '⁇', '‽', '⸘', '❔', '❓')
	
	def isAllMessageMark (using text: String): Boolean = {
		boundary[Boolean] {
			for (c <- text)
				if !(QUESTION_MARKS contains c) then
					boundary break false
			true
		}
	}
	
	object Marked
	
}
