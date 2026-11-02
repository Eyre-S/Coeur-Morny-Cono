package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.command.{ISimpleCommand, ITelegramCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import com.pengrad.telegrambot.model.{BotCommand, Update}
import com.pengrad.telegrambot.request.{DeleteMyCommands, SetMyCommands}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

object MornyCommandManager:
	type CommandMap = mutable.SeqMap[String, ISimpleCommand]

class MornyCommandManager (using coeur: MornyCoeur) extends SimpleCommandManager {
	import coeur.dsl.given
	
	protected override def nonCommandExecutable (using command: InputCommand, event: Update): Boolean = {
		if command.target eq null then false
		else
			Messages.derive(event.message)
				.sticker(TelegramStickers.ID_404)
				.send
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
		list.toArray
	
	private def formatTelegramCommandListLine (command: ITelegramCommand): Array[BotCommand] =
		def buildOne (name: String, paramRule: String, intro: String): BotCommand =
			BotCommand(name, if paramRule isBlank then intro else s"$paramRule - $intro")
		val list = mutable.ArrayBuffer[BotCommand](
			buildOne(command.name, command.paramRule, command.description))
		for (alias <- command.aliases)
			if (alias.listed) list += buildOne(alias.name, "", "↑")
		list.toArray
	
}
