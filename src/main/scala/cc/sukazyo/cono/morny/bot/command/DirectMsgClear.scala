package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.{Chat, Update}
import com.pengrad.telegrambot.request.{DeleteMessage, GetChatMember, SendSticker}

import scala.language.postfixOps

class DirectMsgClear (using coeur: MornyCoeur) extends ISimpleCommand {
	
	override val name: String = "r"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		logger debug "executing command /r"
		if (event.message.replyToMessage == null) return;
		logger trace "message is a reply"
		if (event.message.replyToMessage.from.id != coeur.userid) return;
		logger trace "message replied is from me"
		if (System.currentTimeMillis/1000 - event.message.replyToMessage.date > 48*60*60) return;
		logger trace "message is not outdated(48 hrs ago)"
		
		val isTrusted = coeur.trusted isTrusted event.message.from.id
		// todo:
		//  it does not work. due to the Telegram Bot API doesn't provide
		//  nested replyToMessage, so currently the trusted check by
		//  replyToMessage.replyToMessage will not work!
		def _isReplyTrusted: Boolean =
			if (event.message.replyToMessage.replyToMessage == null) false
			else if (event.message.replyToMessage.replyToMessage.from.id == event.message.from.id) true
			else false
		
		if (isTrusted || _isReplyTrusted) {
		
			coeur.account exec DeleteMessage(
				event.message.chat.id, event.message.replyToMessage.messageId
			)
			
			def _isPrivate: Boolean = event.message.chat.`type` == Chat.Type.Private
			def _isPermission: Boolean =
				(coeur.account exec GetChatMember(event.message.chat.id, event.message.from.id))
						.chatMember.canDeleteMessages
			if (_isPrivate || _isPermission) {
				coeur.account exec DeleteMessage(event.message.chat.id, event.message.messageId)
			}
		
		} else coeur.account exec SendSticker(
			event.message.chat.id,
			TelegramStickers ID_403
		).replyToMessageId(event.message.messageId)
		
	}
	
}
