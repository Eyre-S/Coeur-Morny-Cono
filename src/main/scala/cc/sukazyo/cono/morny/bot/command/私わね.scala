package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.UseMath.over
import cc.sukazyo.cono.morny.util.UseRandom.*
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage

//noinspection NonAsciiCharacters
class 私わね (using coeur: MornyCoeur) extends ISimpleCommand {
	
	override val name: String = "me"
	override val aliases: Array[ICommandAlias] | Null = null
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		if ((1 over 521) chance_is true) {
			val text = "/打假"
			coeur.account exec new SendMessage(
				event.message.chat.id,
				text
			).replyToMessageId(event.message.messageId)
		}
		
	}
	
}
