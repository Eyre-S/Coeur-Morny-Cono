package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.messages
import cc.sukazyo.cono.morny.core.bot.api.messages.MessagingContext
import cc.sukazyo.cono.morny.core.event.TelegramCoreCommandEvents
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.command.ICommandAlias.HiddenAlias
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, InputCommand, ISimpleCommand, ITelegramCommand}
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.{EditMessageText, SendMessage, SendSticker}

class MornyManagers (using coeur: MornyCoeur) {
	import coeur.dsl.{*, given}
	
	private def verifyTrusted (command: ISimpleCommand)(using cxt: MessagingContext.WithUserAndMessage): Boolean = {
		if !(coeur.trusted isTrust cxt.bind_user) then
			SendSticker(
				cxt.bind_chat.id,
				TelegramStickers ID_403
			).replyToMessageId(cxt.bind_message.messageId)
				.unsafeExecute
			logger `attention` s"403 ${command.name} caught from user ${cxt.bind_user toLogTag}"
			TelegramCoreCommandEvents.inCoeur.OnUnauthorizedManageCommandCall.emit((cxt, command))
			false
		else true
	}
	
	object Reload extends ITelegramCommand {
		
		override val name: String = "reload"
		override val aliases: List[ICommandAlias] = Nil
		override val paramRule: String = ""
		override val description: String = "重新载入 Bot 资源文件 / 配置文件"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			given cxt: MessagingContext.WithUserAndMessage = MessagingContext.extract(using event.message)
			
			if !verifyTrusted(this) then return
			
			val statusMessage = SendMessage(
				cxt.bind_chat.id,
				"[ .... ] Coeur reload."
			).replyToMessageId(cxt.bind_message.messageId)
				.unsafeExecute
			
			coeur.reload()
			
			EditMessageText(
				statusMessage.message.chat.id,
				statusMessage.message.messageId,
				"[  OK  ] Coeur reload."
			).unsafeExecute
			
		}
		
	}
	
	object Exit extends ITelegramCommand {
		
		override val name: String = "exit"
		override val aliases: List[ICommandAlias] = HiddenAlias("stop") :: HiddenAlias("quit") :: Nil
		override val paramRule: String = "exit"
		override val description: String = "关闭 Bot （仅可信成员）"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			given cxt: MessagingContext.WithUserAndMessage = MessagingContext.extract(using event.message)
			
			if !verifyTrusted(this) then return
			
			SendSticker(
				cxt.bind_chat.id,
				TelegramStickers ID_EXIT
			).replyToMessageId(cxt.bind_message.messageId)
				.unsafeExecute
			logger `attention` s"Morny exited by user ${cxt.bind_user toLogTag}"
			coeur.exit(0, cxt.bind_user)
			
		}
		
	}
	
	object SaveData extends ITelegramCommand {
		
		override val name: String = "save"
		override val aliases: List[ICommandAlias] = Nil
		override val paramRule: String = ""
		override val description: String = "保存缓存数据到文件（仅可信成员）"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			given cxt: MessagingContext.WithUserAndMessage = MessagingContext.extract(using event.message)
			
			if !verifyTrusted(this) then return
			
			logger `attention` s"call save from command by ${cxt.bind_user toLogTag}"
			coeur.saveDataAll()
			SendSticker(
				cxt.bind_chat.id,
				TelegramStickers ID_SAVED
			).replyToMessageId(cxt.bind_message.messageId)
				.unsafeExecute
			
		}
		
	}
	
}
