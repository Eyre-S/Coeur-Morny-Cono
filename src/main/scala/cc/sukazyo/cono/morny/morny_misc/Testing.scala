package cc.sukazyo.cono.morny.morny_misc

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.core.bot.api.messages.{ErrorMessage, MessagingContext}
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.{Message, Update}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendSticker}

class Testing (using coeur: MornyCoeur) extends ISimpleCommand {
	import coeur.dsl.{*, given}
	
	override val name: String = "test"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		given context: MessagingContext.WithUserAndMessage = MessagingContext.extract(using event.message)
		given lang: String = context.bind_user.prefer_language
		
		coeur.messageThreading.ensureCleanState
		
		SendMessage(
			event.message.chat.id,
			translations.trans("morny.misc.command_test.message")
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
			.unsafeExecute
		
		coeur.messageThreading.doAfter(execute2)
		
	}
	
	private def execute2 (message: Message, previousContext: MessagingContext.WithUserAndMessage): Unit = {
		given context: MessagingContext.WithUserAndMessage = MessagingContext.extract(using message)
		given lang: String = context.bind_user.prefer_language
		
		if (message.text == "oops")
			SendMessage(
				message.chat.id,
				translations.trans("morny.misc.command_test.branch_oops.err_message_simple")
			).replyToMessageId(message.messageId)
				.unsafeExecute
			ErrorMessage(
				_simple = SendSticker(
					message.chat.id,
					TelegramStickers.ID_404
				).replyToMessageId(message.messageId),
				_complex = SendMessage(
					message.chat.id,
					translations.trans("morny.misc.command_test.branch_oops.err_message_complex")
				).replyToMessageId(message.messageId)
			).submit
			return;
		
		SendMessage(
			message.chat.id,
			translations.trans(
				"morny.misc.command_test.branch_normal.message",
				"replied_message" -> message.text
			)
		).replyToMessageId(message.messageId).parseMode(ParseMode HTML)
			.unsafeExecute
		
	}
	
}
