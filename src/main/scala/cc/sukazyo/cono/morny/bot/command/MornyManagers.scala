package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.bot.command.ICommandAlias.HiddenAlias
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.daemon.MornyReport
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.{Chat, Update}
import com.pengrad.telegrambot.request.SendSticker

import scala.language.postfixOps

class MornyManagers (using coeur: MornyCoeur) {
	
	/** Check if the command is directly targeted to Morny.
	  * 
	  * If and only if the command is in private chat, or the command is in group chat AND the command target is explicitly
	  * set to Morny's username, then the command is targeted to Morny.
	  * 
	  * This aims to reduce the mis-trigger on some commands that are easily collisions to other bots and are important.
	  */
	def isMornyTargeted (event: Update, command: InputCommand): Boolean =
		if event.message.chat.`type` != Chat.Type.Private && command.target != coeur.username then false
		else if command.target != null && command.target != coeur.username then false
		else true
	
	object Exit extends ITelegramCommand {
		
		override val name: String = "exit"
		override val aliases: Array[ICommandAlias] | Null = Array(HiddenAlias("stop"), HiddenAlias("quit"))
		override val paramRule: String = "exit"
		override val description: String = "关闭 Bot （仅可信成员）"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			
			if !isMornyTargeted(event, command) then
				logger debug "seems command does not targeted to morny, skipped"
				return
			
			val user = event.message.from
			
			if (coeur.trusted isTrusted user.id) {
				
				coeur.account exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_EXIT
				).replyToMessageId(event.message.messageId)
				logger attention s"Morny exited by user ${user toLogTag}"
				coeur.exit(0, user)
				
			} else {
				
				coeur.account exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_403
				).replyToMessageId(event.message.messageId)
				logger attention s"403 exit caught from user ${user toLogTag}"
				coeur.daemons.reporter.unauthenticatedAction("/exit", user)
				
			}
			
		}
		
	}
	
	object SaveData extends ITelegramCommand {
		
		override val name: String = "save"
		override val aliases: Array[ICommandAlias] | Null = null
		override val paramRule: String = ""
		override val description: String = "保存缓存数据到文件（仅可信成员）"
		
		override def execute (using command: InputCommand, event: Update): Unit = {
			
			if !isMornyTargeted(event, command) then
				logger debug "seems command does not targeted to morny, skipped"
				return
			
			val user = event.message.from
			
			if (coeur.trusted isTrusted user.id) {
				
				logger attention s"call save from command by ${user toLogTag}"
				coeur.saveDataAll()
				coeur.account exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_SAVED
				).replyToMessageId(event.message.messageId)
				
			} else {
				
				coeur.account exec SendSticker(
					event.message.chat.id,
					TelegramStickers ID_403
				).replyToMessageId(event.message.messageId)
				logger attention s"403 save caught from user ${user toLogTag}"
				coeur.daemons.reporter.unauthenticatedAction("/save", user)
				
			}
			
		}
		
	}
	
}
