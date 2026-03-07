package cc.sukazyo.cono.morny.uni_meow

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ISimpleCommand, ITelegramCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import com.pengrad.telegrambot.model.Update

import scala.language.postfixOps

//noinspection NonAsciiCharacters
class 喵呜 (using coeur: MornyCoeur) {
	import coeur.dsl.given
	
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
			Messages.derive(event.message)
				.sticker(TelegramStickers.ID_PROGYNOVA)
				.send
		}
	}
	
	private def replyingSet (whileRec: String, whileNew: String)(using event: Update): Unit = {
		val isNew = event.message.replyToMessage == null
		val target = if (isNew) event.message else event.message.replyToMessage
		Messages.derive(event.message).replyTo(target.messageId)(
			Texts.html(if (isNew) whileNew else whileRec)
		).send
	}
	
}
