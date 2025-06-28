package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

import scala.language.postfixOps

class Testing (using coeur: MornyCoeur) extends ISimpleCommand {
	
	override val name: String = "test"
	override val aliases: Array[ICommandAlias] | Null = null
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		coeur.account exec new SendMessage(
			event.message.chat.id,
			// language=html
			"<b>Just</b> a TEST command that will throws an error."
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
		
		throw RuntimeException("Just an example error.")
		
	}
	
}
