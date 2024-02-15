package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand, ITelegramCommand}
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.{Message, Update}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendSticker}
import com.pengrad.telegrambot.TelegramBot

import javax.swing.text.html.HTML
import scala.annotation.unused
import scala.language.postfixOps

//noinspection NonAsciiCharacters
class 喵呜 (using coeur: MornyCoeur) {
	private given TelegramBot = coeur.account
	
	object 抱抱 extends ISimpleCommand {
		override val name: String = "抱抱"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			replyingSet("贴贴", "贴贴")
	}
	
	object 揉揉 extends ISimpleCommand {
		override val name: String = "揉揉"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			replyingSet("蹭蹭", "摸摸")
	}
	
	object 蹭蹭 extends ISimpleCommand {
		override val name: String = "蹭蹭"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			replyingSet("揉揉", "蹭蹭")
	}
	
	object 贴贴 extends ISimpleCommand {
		override val name: String = "贴贴"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit =
			replyingSet("贴贴", "贴贴")
	}
	
	object Progynova extends ITelegramCommand {
		override val name: String = "install"
		override val aliases: List[ICommandAlias] = Nil
		override val paramRule: String = ""
		override val description: String = "抽取一个神秘盒子"
		override def execute (using command: InputCommand, event: Update): Unit = {
			SendSticker(
				event.message.chat.id,
				TelegramStickers ID_PROGYNOVA
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
		}
	}
	
	private def replyingSet (whileRec: String, whileNew: String)(using event: Update): Unit = {
		val isNew = event.message.replyToMessage == null
		val target = if (isNew) event.message else event.message.replyToMessage
		SendMessage(
			event.message.chat.id,
			if (isNew) whileNew else whileRec
		).replyToMessageId(target.messageId).parseMode(ParseMode HTML)
			.unsafeExecute
	}
	
}
