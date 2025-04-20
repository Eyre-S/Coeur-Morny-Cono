package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, InputCommand, ISimpleCommand}
import cc.sukazyo.cono.morny.util.UseMath.over
import cc.sukazyo.cono.morny.util.UseRandom.*
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import com.pengrad.telegrambot.TelegramBot

import scala.language.implicitConversions

//noinspection NonAsciiCharacters
class 私わね (using coeur: MornyCoeur) extends ISimpleCommand {
	private given TelegramBot = coeur.account
	
	override val name: String = "me"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		if ((1 over 521) chance_is true) {
			val text = "/打假"
			SendMessage(
				event.message.chat.id,
				text
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
		}
		
	}
	
}
