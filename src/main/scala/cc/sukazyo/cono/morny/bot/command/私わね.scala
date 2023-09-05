package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.CommonRandom.probabilityTrue
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage

object 私わね extends ISimpleCommand {
	
	override def getName: String = "me"
	override def getAliases: Array[String] = Array()
	
	override def execute (command: InputCommand, event: Update): Unit = {
		
		if (probabilityTrue(521)) {
			val text = "/打假"
			MornyCoeur.extra exec new SendMessage(
				event.message.chat.id,
				text
			).replyToMessageId(event.message.messageId)
		}
		
	}
	
}
