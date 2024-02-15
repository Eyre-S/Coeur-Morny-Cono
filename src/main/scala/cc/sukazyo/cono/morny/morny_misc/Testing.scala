package cc.sukazyo.cono.morny.morny_misc

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.TelegramBot

import scala.language.postfixOps

class Testing (using coeur: MornyCoeur) extends ISimpleCommand {
	private given TelegramBot = coeur.account
	
	override val name: String = "test"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		SendMessage(
			event.message.chat.id,
			// language=html
			"<b>Just</b> a TEST command."
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
			.unsafeExecute
		
	}
	
}
