package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.bot.command.MornyCommandManager.CommandMap
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.{BotCommand, DeleteMyCommands, Update}
import com.pengrad.telegrambot.request.{SendSticker, SetMyCommands}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

object MornyCommandManager:
	type CommandMap = mutable.SeqMap[String, ISimpleCommand]

class MornyCommandManager (using coeur: MornyCoeur) {
	
	private val commands: CommandMap = mutable.SeqMap.empty
	def register [T <: ISimpleCommand] (commands: T*): Unit =
		for (i <- commands)
			this.commands += (i.name -> i)
			for (alias <- i.aliases)
				this.commands += (alias.name -> i)
	
	def execute (using command: InputCommand, event: Update): Boolean = {
		if (commands contains command.command)
			commands(command.command) execute;
			true
		else nonCommandExecutable
	}
	
	private def nonCommandExecutable (using command: InputCommand, event: Update): Boolean = {
		if command.target eq null then false
		else
			coeur.account exec SendSticker(
				event.message.chat.id,
				TelegramStickers ID_404
			).replyToMessageId(event.message.messageId)
			true
	}
	
	def automaticTGListUpdate (): Unit = {
		val listing = commands_toTelegramList
		automaticTGListRemove()
		coeur.account exec SetMyCommands(listing:_*)
		logger notice
				s"""automatic updated telegram command list :
				   |${commandsTelegramList_toString(listing)}""".stripMargin
	}
	
	def automaticTGListRemove (): Unit = {
		coeur.account exec DeleteMyCommands()
		logger notice "cleaned up command list"
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
