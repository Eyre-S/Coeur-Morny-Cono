package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.event.OnQuestionMarkReply.isAllMessageMark
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps
import scala.util.boundary

class OnQuestionMarkReply (using coeur: MornyCoeur) extends EventListener {
	
	override def onMessage (using event: Update): Boolean = {
		
		if event.message.text eq null then return false
		
		import cc.sukazyo.cono.morny.util.UseMath.over
		import cc.sukazyo.cono.morny.util.UseRandom.chance_is
		if (1 over 8) chance_is false then return false
		if !isAllMessageMark(using event.message.text) then return false
		
		coeur.account exec SendMessage(
			event.message.chat.id, event.message.text
		).replyToMessageId(event.message.messageId)
		true
		
	}
	
}

object OnQuestionMarkReply {
	
	private val QUESTION_MARKS = Set('?', '？', '¿', '⁈', '⁇', '‽', '❔', '❓')
	
	def isAllMessageMark (using text: String): Boolean = {
		boundary[Boolean] {
			for (c <- text) if QUESTION_MARKS contains c then boundary.break(false)
			true
		}
	}
	
}
