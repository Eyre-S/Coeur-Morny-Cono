package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.bot.api.ICommandAlias.HiddenAlias
import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ITelegramCommand}
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.reporter.MornyReport
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendSticker
import com.pengrad.telegrambot.TelegramBot

class MornyManagers (using coeur: MornyCoeur) {
	private given TelegramBot = coeur.account
	
	object Exit extends ITelegramCommand {
		
		override val name: String = "exit"
		override val aliases: List[ICommandAlias] = HiddenAlias("stop") :: HiddenAlias("quit") :: Nil
		override val paramRule: String = "exit"
		override val description: String = "关闭 Bot （仅可信成员）"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			
			val user = event.message.from
			
			if (coeur.trusted isTrust user) {
				
				SendSticker(
					event.message.chat.id,
					TelegramStickers ID_EXIT
				).replyToMessageId(event.message.messageId)
					.unsafeExecute
				logger `attention` s"Morny exited by user ${user toLogTag}"
				coeur.exit(0, user)
				
			} else {
				
				SendSticker(
					event.message.chat.id,
					TelegramStickers ID_403
				).replyToMessageId(event.message.messageId)
					.unsafeExecute
				logger `attention` s"403 exit caught from user ${user toLogTag}"
				coeur.externalContext.consume[MornyReport](_.unauthenticatedAction("/exit", user))
				
			}
			
		}
		
	}
	
	object SaveData extends ITelegramCommand {
		
		override val name: String = "save"
		override val aliases: List[ICommandAlias] = Nil
		override val paramRule: String = ""
		override val description: String = "保存缓存数据到文件（仅可信成员）"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			
			val user = event.message.from
			
			if (coeur.trusted isTrust user) {
				
				logger `attention` s"call save from command by ${user toLogTag}"
				coeur.saveDataAll()
				SendSticker(
					event.message.chat.id,
					TelegramStickers ID_SAVED
				).replyToMessageId(event.message.messageId)
					.unsafeExecute
				
			} else {
				
				SendSticker(
					event.message.chat.id,
					TelegramStickers ID_403
				).replyToMessageId(event.message.messageId)
					.unsafeExecute
				logger `attention` s"403 save caught from user ${user toLogTag}"
				coeur.externalContext.consume[MornyReport](_.unauthenticatedAction("/save", user))
				
			}
			
		}
		
	}
	
}
