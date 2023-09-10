package cc.sukazyo.cono.morny.bot.command
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.event.OnEventHackHandle
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update
import OnEventHackHandle.{HackType, registerHack}
import cc.sukazyo.cono.morny.data.TelegramStickers
import com.pengrad.telegrambot.request.SendSticker

import scala.language.postfixOps

object EventHack extends ITelegramCommand {
	
	override val name: String = "event_hack"
	override val aliases: Array[ICommandAlias] | Null = null
	override val paramRule: String = "[(user|group|any)]"
	override val description: String = "输出 bot 下一个获取到的事件序列化数据"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		val x_mode = if (command.hasArgs) command.getArgs()(0) else ""
		
		def done_ok =
			MornyCoeur.extra exec SendSticker(
				event.message.chat.id,
				TelegramStickers ID_WAITING
			).replyToMessageId(event.message.messageId)
		def done_forbiddenForAny =
			MornyCoeur.extra exec SendSticker(
				event.message.chat.id,
				TelegramStickers ID_403
			).replyToMessageId(event.message.messageId)
		
		def doRegister (t: HackType): Unit =
			registerHack(
				event.message.messageId longValue,
				event.message.from.id,
				event.message.chat.id,
				t
			)
		x_mode match
			case "any" =>
				if (MornyCoeur.trusted isTrusted event.message.from.id)
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
