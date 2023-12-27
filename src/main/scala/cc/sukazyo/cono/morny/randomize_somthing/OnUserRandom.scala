package cc.sukazyo.cono.morny.randomize_somthing

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

import scala.language.postfixOps

class OnUserRandom (using coeur: MornyCoeur) {
	
	object RandomSelect extends EventListener {
		
		private val USER_OR_QUERY = "^(.+)(?:还是|or)(.+)$" r
		private val USER_IF_QUERY = "^(.+)(?:吗\\?|？|\\?|吗？)$" r
		
		override def onMessage (using event: EventEnv): Unit = {
			import event.update
			
			if update.message.text == null then return;
			if !(update.message.text startsWith "/") then return
			
			import cc.sukazyo.cono.morny.util.UseRandom.rand_half
			val query = update.message.text substring 1
			val result: String | Null = query match
				case USER_OR_QUERY(_con1, _con2) =>
					if rand_half then _con1 else _con2
				case USER_IF_QUERY(_con) =>
					// for capability with [[OnQuestionMarkReply]]
					if OnQuestionMarkReply.isAllMessageMark(using _con) then return;
					(if rand_half then "不" else "") + _con
				case _ => null
			
			if result == null then return;
			
			coeur.account exec SendMessage(
				update.message.chat.id,
				result
			).replyToMessageId(update.message.messageId)
			event.setEventOk
			
		}
		
	}
	
	//noinspection NonAsciiCharacters
	object 尊嘟假嘟 extends EventListener {
		
		private val word_pattern = "^([\\w\\W]*)?(?:尊嘟假嘟|(?:O\\.o|o\\.O))$"r
		private val keywords = Array("尊嘟假嘟", "O.o", "o.O")
		
		override def onMessage (using event: EventEnv): Unit = {
			import event.update
			
			if update.message.text == null then return
			
			var result: String|Null = null
			import cc.sukazyo.cono.morny.util.UseRandom.rand_half
			for (k <- keywords)
				if update.message.text endsWith k then
					result = if rand_half then "尊嘟" else "假嘟"
			if result == null then return;
			
			coeur.account exec SendMessage(
				update.message.chat.id,
				result
			).replyToMessageId(update.message.messageId)
			event.setEventOk
			
		}
		
	}
	
}
