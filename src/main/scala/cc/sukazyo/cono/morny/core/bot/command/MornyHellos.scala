package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener, ICommandAlias, ITelegramCommand}
import cc.sukazyo.cono.morny.core.bot.api.ICommandAlias.ListedAlias
import cc.sukazyo.cono.morny.core.bot.api.messages.MessagingContext
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Chat.notOfType
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.{Chat, Update}
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
			this.sendSticker(using MessagingContext.extract(using event.message))
		
		def sendSticker (using cxt: MessagingContext.WithMessage): Unit =
			SendSticker(
				cxt.bind_chat.id,
				TelegramStickers ID_ONLINE_STATUS_RETURN
			).replyToMessageId(cxt.bind_message.messageId)
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
	
	object PrivateChat_O extends EventListener {
		
		override def onMessage (using event: EventEnv): Unit = {
			import event.update
			
			if update.message.chat notOfType Chat.Type.Private then
				return;
			if update.message.text == "o" || update.message.text == "O" then
				On.sendSticker(using MessagingContext.extract(using update.message))
			
		}
		
	}
	
}
