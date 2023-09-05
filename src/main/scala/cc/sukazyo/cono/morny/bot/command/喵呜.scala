package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.model.{Message, Update}
import com.pengrad.telegrambot.request.{SendMessage, SendSticker}

import javax.swing.text.html.HTML
import scala.annotation.unused
import scala.language.postfixOps

@SuppressWarnings(Array("NonAsciiCharacters"))
object 喵呜 {
	
	object 抱抱 extends ISimpleCommand {
		override def getName: String = "抱抱"
		override def getAliases: Array[String] = Array()
		override def execute (command: InputCommand, event: Update): Unit =
			replyingSet(event, "贴贴", "贴贴")
	}
	
	object 揉揉 extends ISimpleCommand {
		override def getName: String = "揉揉"
		override def getAliases: Array[String] = Array()
		override def execute (command: InputCommand, event: Update): Unit =
			replyingSet(event, "蹭蹭", "摸摸")
	}
	
	object 蹭蹭 extends ISimpleCommand {
		override def getName: String = "蹭蹭"
		override def getAliases: Array[String] = Array()
		override def execute (command: InputCommand, event: Update): Unit =
			replyingSet(event, "揉揉", "蹭蹭")
	}
	
	object 贴贴 extends ISimpleCommand {
		override def getName: String = "贴贴"
		override def getAliases: Array[String] = Array()
		override def execute (command: InputCommand, event: Update): Unit =
			replyingSet(event, "贴贴", "贴贴")
	}
	
	object Progynova extends ITelegramCommand {
		override def getName: String = "install"
		override def getAliases: Array[String] = Array()
		override def getParamRule: String = ""
		override def getDescription: String = "抽取一个神秘盒子"
		override def execute (command: InputCommand, event: Update): Unit = {
			MornyCoeur.extra exec new SendSticker(
				event.message.chat.id,
				TelegramStickers ID_PROGYNOVA
			).replyToMessageId(event.message.messageId)
		}
	}
	
	private def replyingSet (event: Update, whileRec: String, whileNew: String): Unit = {
		val isNew = event.message.replyToMessage == null;
		val target = if (isNew) event.message else event.message.replyToMessage
		MornyCoeur.extra exec new SendMessage(
			event.message.chat.id,
			if (isNew) whileNew else whileRec
		).replyToMessageId(target.messageId).parseMode(ParseMode HTML)
	}
	
}
