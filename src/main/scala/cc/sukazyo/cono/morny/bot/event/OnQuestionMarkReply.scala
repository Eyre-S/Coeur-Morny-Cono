package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.event.OnQuestionMarkReply.isAllMessageMark
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps
import scala.util.boundary

class OnQuestionMarkReply (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: EventEnv): Unit = {
		import event.update
		
		if update.message.text eq null then return
		
		import cc.sukazyo.cono.morny.util.UseMath.over
		import cc.sukazyo.cono.morny.util.UseRandom.chance_is
		if (1 over 8) chance_is false then return;
		if !isAllMessageMark(using update.message.text) then return;
		
		coeur.account exec SendMessage(
			update.message.chat.id, update.message.text
		).replyToMessageId(update.message.messageId)
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
	
}
