package cc.sukazyo.cono.morny.tele_utils.event_hack

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ITelegramCommand}
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendSticker
import com.pengrad.telegrambot.TelegramBot

import scala.language.postfixOps

class CommandEventHack (using hacker: EventHacker)(using coeur: MornyCoeur) extends ITelegramCommand {
	private given TelegramBot = coeur.account
	
	override val name: String = "event_hack"
	override val aliases: List[ICommandAlias] = Nil
	override val paramRule: String = "[(user|group|any)]"
	override val description: String = "输出 bot 下一个获取到的事件序列化数据"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		import hacker.{registerHack, HackType}
		
		val x_mode = if (command.args nonEmpty) command.args(0) else ""
		
		def done_ok =
			SendSticker(
				event.message.chat.id,
				TelegramStickers ID_WAITING
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
		def done_forbiddenForAny =
			SendSticker(
				event.message.chat.id,
				TelegramStickers ID_403
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
		
		def doRegister (t: HackType): Unit =
			registerHack(
				event.message.messageId longValue,
				event.message.from.id,
				event.message.chat.id,
				t
			)
		x_mode match
			case "any" =>
				if (coeur.trusted isTrust event.message.from)
					doRegister(HackType ANY)
					done_ok
				else done_forbiddenForAny
			case "group" =>
				doRegister(HackType GROUP)
				done_ok
			case _ =>
				doRegister(HackType USER)
				done_ok
		
	}
	
}