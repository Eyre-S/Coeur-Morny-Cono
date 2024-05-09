package cc.sukazyo.cono.morny.randomize_somthing

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.randomize_somthing.OnQuestionMarkReply.isAllMessageMark
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.TelegramBot

import scala.util.boundary
import scala.util.boundary.break

class OnQuestionMarkReply (using coeur: MornyCoeur) extends EventListener {
	private given TelegramBot = coeur.account
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update
		
		if update.message.text eq null then return
		
		import cc.sukazyo.cono.morny.util.UseMath.over
		import cc.sukazyo.cono.morny.util.UseRandom.chance_is
		if (1 over 8) chance_is false then return;
		if !isAllMessageMark(using update.message.text) then return;
		
		SendMessage(
			update.message.chat.id, update.message.text
		).replyToMessageId(update.message.messageId)
			.unsafeExecute
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
					break(false)
			true
		}
	}
	
}