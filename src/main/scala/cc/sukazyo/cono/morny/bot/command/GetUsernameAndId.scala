package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.{InputCommand, Standardize}
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramUserInformation
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{GetChatMember, SendMessage}

import scala.language.postfixOps

class GetUsernameAndId (using coeur: MornyCoeur) extends ITelegramCommand {
	
	override val name: String = "user"
	override val aliases: List[ICommandAlias] = Nil
	override val paramRule: String = "[userid]"
	override val description: String = "获取指定或回复的用户相关信息"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		val args = command.args
		
		if (args.length > 1)
			coeur.account exec SendMessage(
				event.message.chat.id,
				"[Unavailable] Too much arguments."
			).replyToMessageId(event.message.messageId)
			return
		
		val userId: Long =
			if (args nonEmpty) {
				try args(0) toLong
				catch case e: NumberFormatException =>
					coeur.account exec SendMessage(
						event.message.chat.id,
						s"[Unavailable] ${e.getMessage}"
					).replyToMessageId(event.message.messageId)
					return
			} else if (event.message.replyToMessage eq null) event.message.from.id
			else event.message.replyToMessage.from.id
		
		val response = coeur.account execute GetChatMember(event.message.chat.id, userId)
		
		if (response.chatMember eq null)
			coeur.account exec SendMessage(
				event.message.chat.id,
				"[Unavailable] user not found."
			).replyToMessageId(event.message.messageId)
			return
		
		val user = response.chatMember.user
		
		if (user.id == Standardize.CHANNEL_SPEAKER_MAGIC_ID)
			coeur.account exec SendMessage(
				event.message.chat.id,
				"<code>$__channel_identify</code>"
			).replyToMessageId(event.message.messageId)
			return;
		
		coeur.account exec SendMessage(
			event.message.chat.id,
			TelegramUserInformation getFormattedInformation user
		).replyToMessageId(event.message.messageId()).parseMode(ParseMode HTML)
		
	}
	
}
