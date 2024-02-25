package cc.sukazyo.cono.morny.morny_misc

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.core.bot.api.messages.{ErrorMessage, MessagingContext}
import cc.sukazyo.cono.morny.core.bot.api.BotExtension.submit
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.{Message, Update}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendSticker}
import com.pengrad.telegrambot.TelegramBot

class Testing (using coeur: MornyCoeur) extends ISimpleCommand {
	private given TelegramBot = coeur.account
	
	override val name: String = "test"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		given context: MessagingContext.WithUserAndMessage = MessagingContext.extract(using event.message)
		
		SendMessage(
			event.message.chat.id,
			// language=html
			"<b>Just</b> a TEST command.\n"
				+ "Please input something to test the command."
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
			.unsafeExecute
		
		coeur.messageThreading.doAfter(execute2)
		
	}
	
	private def execute2 (message: Message, previousContext: MessagingContext.WithUserAndMessage): Unit = {
		
		if (message.text == "oops")
			SendMessage(
				message.chat.id,
				"A test error message will be generated."
			).replyToMessageId(message.messageId)
				.unsafeExecute
			ErrorMessage(
				_simple = SendSticker(
					message.chat.id,
					TelegramStickers.ID_404
				).replyToMessageId(message.messageId),
				_complex = SendMessage(
					message.chat.id,
					"Oops: There is just a test error."
				).replyToMessageId(message.messageId)
			)(using MessagingContext.extract(using message))
				.submit
			return;
		
		SendMessage(
			message.chat.id,
			// language=html
			"<b><u>Test command with following input:</u></b>\n" + message.text
		).replyToMessageId(message.messageId).parseMode(ParseMode HTML)
			.unsafeExecute
		
	}
	
}
