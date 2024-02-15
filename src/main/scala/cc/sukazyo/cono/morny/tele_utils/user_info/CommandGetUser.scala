package cc.sukazyo.cono.morny.tele_utils.user_info

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ITelegramCommand}
import cc.sukazyo.cono.morny.util.tgapi.{InputCommand, Standardize}
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramUserInformation
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.{execute, unsafeExecute}
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{GetChatMember, SendMessage}
import com.pengrad.telegrambot.TelegramBot

class CommandGetUser (using coeur: MornyCoeur) extends ITelegramCommand {
	private given TelegramBot = coeur.account
	
	override val name: String = "user"
	override val aliases: List[ICommandAlias] = Nil
	override val paramRule: String = "[userid]"
	override val description: String = "获取指定或回复的用户相关信息"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		val args = command.args
		
		if (args.length > 1)
			SendMessage(
				event.message.chat.id,
				"[Unavailable] Too much arguments."
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
			return
		
		val userId: Long =
			if (args nonEmpty) {
				try args(0) toLong
				catch case e: NumberFormatException =>
					SendMessage(
						event.message.chat.id,
						s"[Unavailable] ${e.getMessage}"
					).replyToMessageId(event.message.messageId)
						.unsafeExecute
					return
			} else if (event.message.replyToMessage eq null) event.message.from.id
			else event.message.replyToMessage.from.id
		
		val response = GetChatMember(event.message.chat.id, userId).execute
		
		if (response.chatMember eq null)
			SendMessage(
				event.message.chat.id,
				"[Unavailable] user not found."
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
			return
		
		val user = response.chatMember.user
		
		if (user.id == Standardize.CHANNEL_SPEAKER_MAGIC_ID)
			SendMessage(
				event.message.chat.id,
				"<code>$__channel_identify</code>"
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
			return;
		
		SendMessage(
			event.message.chat.id,
			TelegramUserInformation.getFormattedInformation(user)
		).replyToMessageId(event.message.messageId()).parseMode(ParseMode HTML)
			.unsafeExecute
		
	}
	
}
