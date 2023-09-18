package cc.sukazyo.cono.morny.bot.command
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.command.ICommandAlias.ListedAlias
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendSticker

import scala.language.postfixOps

object MornyHellos {
	
	object On extends ITelegramCommand {
		
		override val name: String = "on"
		override val aliases: Array[ICommandAlias] | Null = null
		override val paramRule: String = ""
		override val description: String = "检查是否在线"
		
		override def execute (using command: InputCommand, event: Update): Unit =
			MornyCoeur.extra exec SendSticker(
				event.message.chat.id,
				TelegramStickers ID_ONLINE_STATUS_RETURN
			).replyToMessageId(event.message.messageId)
		
	}
	
	object Hello extends ITelegramCommand {
		
		override val name: String = "hello"
		override val aliases: Array[ICommandAlias] | Null = Array(ListedAlias("hi"))
		override val paramRule: String = ""
		override val description: String = "打招呼"
		
		override def execute (using command: InputCommand, event: Update): Unit =
			MornyCoeur.extra exec SendSticker(
				event.message.chat.id,
				TelegramStickers ID_HELLO
			).replyToMessageId(event.message.messageId)
		
	}
	
}
