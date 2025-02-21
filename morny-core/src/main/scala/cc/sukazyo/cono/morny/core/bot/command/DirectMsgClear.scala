package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, InputCommand, ISimpleCommand}
import com.pengrad.telegrambot.model.{Chat, Update}
import com.pengrad.telegrambot.request.{DeleteMessage, GetChatMember, SendSticker}
import com.pengrad.telegrambot.TelegramBot

import scala.language.postfixOps

class DirectMsgClear (using coeur: MornyCoeur) extends ISimpleCommand {
	private given TelegramBot = coeur.account
	
	override val name: String = "r"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		logger `debug` "executing command /r"
		if (event.message.replyToMessage == null) return
		logger `trace` "message is a reply"
		if (event.message.replyToMessage.from.id != coeur.userid) return
		logger `trace` "message replied is from me"
		if (System.currentTimeMillis/1000 - event.message.replyToMessage.date > 48*60*60) return
		logger `trace` "message is not outdated(48 hrs ago)"
		
		val isTrusted = coeur.trusted isTrust event.message.from
		// todo:
		//  it does not work. due to the Telegram Bot API doesn't provide
		//  nested replyToMessage, so currently the trusted check by
		//  replyToMessage.replyToMessage will not work!
		def _isReplyTrusted: Boolean =
			if (event.message.replyToMessage.replyToMessage == null) false
			else if (event.message.replyToMessage.replyToMessage.from.id == event.message.from.id) true
			else false
		
		if (isTrusted || _isReplyTrusted) {
		
			DeleteMessage(
				event.message.chat.id, event.message.replyToMessage.messageId
			).unsafeExecute
			
			def _isPrivate: Boolean = event.message.chat.`type` == Chat.Type.Private
			def _isPermission: Boolean =
				GetChatMember(event.message.chat.id, event.message.from.id).unsafeExecute
						.chatMember.canDeleteMessages
			if (_isPrivate || _isPermission) {
				DeleteMessage(event.message.chat.id, event.message.messageId).unsafeExecute
			}
		
		} else SendSticker(
			event.message.chat.id,
			TelegramStickers ID_403
		).replyToMessageId(event.message.messageId).unsafeExecute
		
	}
	
}
