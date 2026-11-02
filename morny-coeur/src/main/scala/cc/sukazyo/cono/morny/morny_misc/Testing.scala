package cc.sukazyo.cono.morny.morny_misc

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.messages.{ErrorMessage, MessagingContext}
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ISimpleCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import com.pengrad.telegrambot.model.{Message, Update}

class Testing (using coeur: MornyCoeur) extends ISimpleCommand {
	import coeur.dsl.{*, given}
	
	override val name: String = "test"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		given context: MessagingContext.WithUserAndMessage = MessagingContext.extract(using event.message)
		given lang: String = context.bind_user.prefer_language
		
		coeur.messageThreading.ensureCleanState
		
		Messages.derive(event.message)(Texts.html(
			translations.trans("morny.misc.command_test.message")
		)).send
		
		coeur.messageThreading.doAfter(execute2)
		
	}
	
	private def execute2 (message: Message, previousContext: MessagingContext.WithUserAndMessage): Unit = {
		given context: MessagingContext.WithUserAndMessage = MessagingContext.extract(using message)
		given lang: String = context.bind_user.prefer_language
		// TODO: upgrade to new MessagingContext API
		val ccMsg = Messages.derive(message)
		
		if (message.text == "oops")
			ccMsg(translations.trans("morny.misc.command_test.branch_oops.err_message_simple")).send
			ErrorMessage(
				_simple =
					ccMsg.sticker(TelegramStickers.ID_404)
						.getSendRequest.request,
				_complex =
					ccMsg(translations.trans("morny.misc.command_test.branch_oops.err_message_complex"))
						.getSendRequest.request
			).submit
			return;
		
		ccMsg(Texts.html(
			translations.trans(
				"morny.misc.command_test.branch_normal.message",
				"replied_message" -> message.text
			)
		)).send
		
	}
	
}
