package cc.sukazyo.cono.morny.bot.command
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramUserInformation
import cc.sukazyo.cono.morny.util.tgapi.{InputCommand, Standardize}
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{GetChatMember, SendMessage}

import scala.language.postfixOps

object GetUsernameAndId extends ITelegramCommand {
	
	override def getName: String = "user"
	override def getAliases: Array[String] = Array()
	override def getParamRule: String = "[userid]"
	override def getDescription: String = "获取指定或回复的用户相关信息"
	
	override def execute (command: InputCommand, event: Update): Unit = {
		
		val args = command.getArgs
		
		if (args.length > 1)
			MornyCoeur.extra exec SendMessage(
				event.message.chat.id,
				"[Unavailable] Too much arguments."
			).replyToMessageId(event.message.messageId)
			return
		
		val userId: Long =
			if (args nonEmpty) {
				try args(0) toLong
				catch case e: NumberFormatException =>
					MornyCoeur.extra exec SendMessage(
						event.message.chat.id,
						s"[Unavailable] ${e.getMessage}"
					).replyToMessageId(event.message.messageId)
					return
			} else if (event.message.replyToMessage eq null) event.message.from.id
			else event.message.replyToMessage.from.id
		
		val response = MornyCoeur.getAccount execute GetChatMember(event.message.chat.id, userId)
		
		if (response.chatMember eq null)
			MornyCoeur.extra exec SendMessage(
				event.message.chat.id,
				"[Unavailable] user not found."
			).replyToMessageId(event.message.messageId)
			return
		
		val user = response.chatMember.user
		
		if (user.id eq Standardize.CHANNEL_SPEAKER_MAGIC_ID)
			MornyCoeur.extra exec SendMessage(
				event.message.chat.id,
				"<code>$__channel_identify</code>"
			).replyToMessageId(event.message.messageId)
			return;
		
		MornyCoeur.extra exec SendMessage(
			event.message.chat.id,
			TelegramUserInformation informationOutputHTML user
		).replyToMessageId(event.message.messageId()).parseMode(ParseMode HTML)
		
	}
	
}
