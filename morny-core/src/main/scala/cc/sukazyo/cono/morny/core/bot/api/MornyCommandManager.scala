package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.command.{InputCommand, ISimpleCommand, ITelegramCommand}
import com.pengrad.telegrambot.model.{BotCommand, DeleteMyCommands, Update}
import com.pengrad.telegrambot.request.{SendSticker, SetMyCommands}
import com.pengrad.telegrambot.TelegramBot

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

object MornyCommandManager:
	type CommandMap = mutable.SeqMap[String, ISimpleCommand]

class MornyCommandManager (using coeur: MornyCoeur) extends SimpleCommandManager {
	private given TelegramBot = coeur.account
	
	protected override def nonCommandExecutable (using command: InputCommand, event: Update): Boolean = {
		if command.target eq null then false
		else
			SendSticker(
				event.message.chat.id,
				TelegramStickers ID_404
			).replyToMessageId(event.message.messageId)
				.unsafeExecute
			true
	}
	
	def automaticTGListUpdate (): Unit = {
		val listing = commands_toTelegramList
		automaticTGListRemove()
		SetMyCommands(listing*)
			.unsafeExecute
		logger `notice`
				s"""automatic updated telegram command list :
				   |${commandsTelegramList_toString(listing)}""".stripMargin
	}
	
	def automaticTGListRemove (): Unit = {
		DeleteMyCommands()
			.unsafeExecute
		logger `notice` "cleaned up command list"
	}
	
	private def commandsTelegramList_toString (list: Array[BotCommand]): String =
		val builder = StringBuilder()
		for (single <- list)
			builder ++= s"${single.command} - ${single.description}\n"
		(builder dropRight 1) toString
	
	private def commands_toTelegramList: Array[BotCommand] =
		val list = ArrayBuffer.empty[BotCommand]
		for ((name, command) <- commands) command match
			case telegramCommand: ITelegramCommand if name == command.name =>
				list ++= formatTelegramCommandListLine(telegramCommand)
			case _ =>
		list toArray
	
	private def formatTelegramCommandListLine (command: ITelegramCommand): Array[BotCommand] =
		def buildOne (name: String, paramRule: String, intro: String): BotCommand =
			BotCommand(name, if paramRule isBlank then intro else s"$paramRule - $intro")
		val list = mutable.ArrayBuffer[BotCommand](
			buildOne(command.name, command.paramRule, command.description))
		for (alias <- command.aliases)
			if (alias.listed) list += buildOne(alias.name, "", "↑")
		list toArray
	
}
