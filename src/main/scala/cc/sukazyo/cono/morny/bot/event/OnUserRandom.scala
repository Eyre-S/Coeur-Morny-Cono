package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.EventListener
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps

object OnUserRandom extends EventListener {

	private val USER_OR_QUERY = "(.+)(?:还是|or)(.+)"r
	private val USER_IF_QUERY = "(.+)[吗?|？]+$"r
	
	override def onMessage(using update: Update): Boolean = {
		
		if update.message.text == null then return false
		if update.message.text startsWith "/" then return false
		
		import cc.sukazyo.cono.morny.util.UseRandom.rand_half
		val query = update.message.text substring 1
		val result: String|Null = query match
			case USER_OR_QUERY(_con1, _con2) =>
				if rand_half then _con1 else _con2
			case USER_IF_QUERY(_con) =>
				(if rand_half then "不" else "") + _con
			case _ => null
		
		if result == null then return false
		
		MornyCoeur.extra exec SendMessage(
			update.message.chat.id, result
		).replyToMessageId(update.message.messageId)
		true
		
	}
	
}
