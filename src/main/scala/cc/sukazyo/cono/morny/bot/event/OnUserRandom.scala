package cc.sukazyo.cono.morny.bot.event

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.api.EventListener
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.response.SendResponse

import scala.language.postfixOps

class OnUserRandom (using coeur: MornyCoeur) {
	
	object RandomSelect extends EventListener {
		
		
		private val USER_OR_QUERY = "^(.+)(?:还是|or)(.+)$" r
		private val USER_IF_QUERY = "^(.+)(?:吗\\?|？|\\?|吗？)$" r
		
		override def onMessage (using update: Update): Boolean = {
			
			if update.message.text == null then return false
			if !(update.message.text startsWith "/") then return false
			
			import cc.sukazyo.cono.morny.util.UseRandom.rand_half
			val query = update.message.text substring 1
			val result: String | Null = query match
				case USER_OR_QUERY(_con1, _con2) =>
					if rand_half then _con1 else _con2
				case USER_IF_QUERY(_con) =>
					// for capability with [[OnQuestionMarkReply]]
					if OnQuestionMarkReply.isAllMessageMark(using _con) then return false
					(if rand_half then "不" else "") + _con
				case _ => null
			
			//noinspection DuplicatedCode
			if result == null then return false
			
			coeur.account exec SendMessage(
				update.message.chat.id, result
			).replyToMessageId(update.message.messageId)
			true
			
		}
		
	}
	
	//noinspection NonAsciiCharacters
	object 尊嘟假嘟 extends EventListener {
		
		private val word_pattern = "^([\\w\\W]*)?(?:尊嘟假嘟|(?:O\\.o|o\\.O))$"r
		private val keywords = Array("尊嘟假嘟", "O.o", "o.O")
		
		override def onMessage (using event: Update): Boolean = {
			
			if event.message.text == null then return false
			
			var result: String|Null = null
			import cc.sukazyo.cono.morny.util.UseRandom.rand_half
			for (k <- keywords)
				if event.message.text endsWith k then
					result = if rand_half then "尊嘟" else "假嘟"
			//noinspection DuplicatedCode
			if result == null then return false
			
			coeur.account exec SendMessage(
				event.message.chat.id,
				result
			).replyToMessageId(event.message.messageId)
			true
			
		}
		
	}
	
}
