package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.messages.MessagingContext
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Chat.notOfType
import cc.sukazyo.cono.morny.system.telegram_api.command.ICommandAlias.ListedAlias
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ITelegramCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import com.pengrad.telegrambot.model.{Chat, Update}

import scala.language.postfixOps

class MornyHellos (using coeur: MornyCoeur) {
	import coeur.dsl.given
	
	object On extends ITelegramCommand {
		
		override val name: String = "on"
		override val aliases: List[ICommandAlias] = Nil
		override val paramRule: String = ""
		override val description: String = "检查是否在线"
		
		override def execute (using command: InputCommand, event: Update): Unit =
			this.sendSticker(using MessagingContext.extract(using event.message))
		
		def sendSticker (using cxt: MessagingContext.WithMessage): Unit = {
			// TODO: can update with new MessagingContext API
			Messages.derive(cxt.bind_message)
				.sticker(TelegramStickers.ID_ONLINE_STATUS_RETURN)
				.send
		}
		
	}
	
	object Hello extends ITelegramCommand {
		
		override val name: String = "hello"
		override val aliases: List[ICommandAlias] = ListedAlias("hi") :: Nil
		override val paramRule: String = ""
		override val description: String = "打招呼"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			Messages.derive(event.message)
				.sticker(TelegramStickers.ID_HELLO)
				.send
		}
		
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
