package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.MornyCoeur
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps

object OnQuestionMarkReply extends EventListener {
	
	private val QUESTION_MARKS = Set('?', '？', '¿', '⁈', '⁇', '‽', '❔', '❓')
	
	def isAllMessageMark (using text: String): Boolean = {
		var isAll = true
		for (c <- text)
			if !(QUESTION_MARKS contains c) then isAll = false
		isAll
	}
	
	override def onMessage (using event: Update): Boolean = {
		
		if event.message.text eq null then return false
		
		import cc.sukazyo.cono.morny.util.UseMath.over
		import cc.sukazyo.cono.morny.util.UseRandom.chance_is
		if (1 over 8) chance_is false then return false
		if !isAllMessageMark(using event.message.text) then return false
		
		MornyCoeur.extra exec SendMessage(
			event.message.chat.id, event.message.text
		).replyToMessageId(event.message.messageId)
		true
		
	}
	
}
