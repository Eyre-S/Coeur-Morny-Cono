package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ITelegramCommand}
import cc.sukazyo.cono.morny.core.bot.api.ICommandAlias.ListedAlias
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendSticker
import com.pengrad.telegrambot.TelegramBot

import scala.language.postfixOps

class MornyHellos (using coeur: MornyCoeur) {
	private given TelegramBot = coeur.account
	
	object On extends ITelegramCommand {
		
		override val name: String = "on"
		override val aliases: List[ICommandAlias] = Nil
		override val paramRule: String = ""
		override val description: String = "检查是否在线"
		
		override def execute (using command: InputCommand, event: Update): Unit =
			SendSticker(
				event.message.chat.id,
				TelegramStickers ID_ONLINE_STATUS_RETURN
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
		
	}
	
	object Hello extends ITelegramCommand {
		
		override val name: String = "hello"
		override val aliases: List[ICommandAlias] = ListedAlias("hi") :: Nil
		override val paramRule: String = ""
		override val description: String = "打招呼"
		
		override def execute (using command: InputCommand, event: Update): Unit =
			SendSticker(
				event.message.chat.id,
				TelegramStickers ID_HELLO
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
		
	}
	
}
