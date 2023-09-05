package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

import javax.annotation.Nonnull
import javax.annotation.Nullable
import scala.language.postfixOps

object Testing extends ISimpleCommand {
	
	override def getName: String = "test"
	override def getAliases: Array[String] = null
	
	override def execute (command: InputCommand, event: Update): Unit = {
		
		val a = StringBuilder("value")
		a ++= "Changed"
		
		MornyCoeur.extra exec new SendMessage(
			event.message.chat.id,
			"<b>Just</b> a TEST command. num is:" + (a toString)
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
		
	}
	
}
