package cc.sukazyo.cono.morny.bot.command
import cc.sukazyo.cono.morny.bot.command.ICommandAlias.HiddenAlias
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.formatting.TGToString
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendSticker

import scala.language.postfixOps
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.daemon.MornyReport

object MornyManagers {
	
	object Exit extends ITelegramCommand {
		
		override val name: String = "exit"
		override val aliases: Array[ICommandAlias] | Null = Array(HiddenAlias("stop"), HiddenAlias("quit"))
		override val paramRule: String = "exit"
		override val description: String = "关闭 Bot （仅可信成员）"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			
			val user = event.message.from
			
			if (MornyCoeur.trustedInstance isTrusted user.id) {
				
				MornyCoeur.extra exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_EXIT
				).replyToMessageId(event.message.messageId)
				logger info s"Morny exited by user ${(TGToString as user) toStringLogTag}"
				MornyCoeur.exit(0, user)
				
			} else {
				
				MornyCoeur.extra exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_403
				).replyToMessageId(event.message.messageId)
				logger info s"403 exit caught from user ${(TGToString as user) toStringLogTag}"
				MornyReport.unauthenticatedAction("/exit", user)
				
			}
			
		}
		
	}
	
	object SaveData extends ITelegramCommand {
		
		override val name: String = "save"
		override val aliases: Array[ICommandAlias] | Null = null
		override val paramRule: String = ""
		override val description: String = "保存缓存数据到文件（仅可信成员）"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			
			val user = event.message.from
			
			if (MornyCoeur.trustedInstance isTrusted user.id) {
				
				logger info s"call save from command by ${(TGToString as user) toStringLogTag}"
				MornyCoeur.callSaveData()
				MornyCoeur.extra exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_SAVED
				).replyToMessageId(event.message.messageId)
				
			} else {
				
				MornyCoeur.extra exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_403
				).replyToMessageId(event.message.messageId)
				logger info s"403 save caught from user ${(TGToString as user) toStringLogTag}"
				MornyReport.unauthenticatedAction("/save", user)
				
			}
			
		}
		
	}
	
}
