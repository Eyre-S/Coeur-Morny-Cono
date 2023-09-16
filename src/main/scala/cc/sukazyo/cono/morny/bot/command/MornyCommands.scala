package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.{BotCommand, DeleteMyCommands, Update}
import com.pengrad.telegrambot.request.{SendSticker, SetMyCommands}

import scala.collection.{mutable, SeqMap}
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

object MornyCommands {
	
	private type CommandMap = SeqMap[String, ISimpleCommand]
	private def CommandMap (commands: ISimpleCommand*): CommandMap =
		val stash = mutable.SeqMap.empty[String, ISimpleCommand]
		for (i <- commands)
			stash += (i.name -> i)
			if (i.aliases ne null) for (alias <- i.aliases)
				stash += (alias.name -> i)
		stash
	
	private val commands: CommandMap = CommandMap(
		
		MornyHellos.On,
		MornyHellos.On,
		MornyHellos.Hello,
		MornyInfoOnStart,
		GetUsernameAndId,
		EventHack,
		Nbnhhsh,
		IP186Query.IP,
		IP186Query.Whois,
		Encryptor,
		MornyManagers.SaveData,
		MornyInformation,
		MornyInformationOlds.Version,
		MornyInformationOlds.Runtime,
		MornyOldJrrp,
		MornyManagers.Exit,
		
		Testing,
		DirectMsgClear,
		
		//noinspection NonAsciiCharacters
		私わね,
		//noinspection NonAsciiCharacters
		喵呜.Progynova
		
	)
	
	//noinspection NonAsciiCharacters
	val commands_uni: CommandMap = CommandMap(
		喵呜.抱抱,
		喵呜.揉揉,
		喵呜.贴贴,
		喵呜.蹭蹭
	)
	
	def execute (using command: InputCommand, event: Update): Boolean = {
		if (commands contains command.command)
			commands(command.command) execute;
			true
		else nonCommandExecutable
	}
	
	private def nonCommandExecutable (using command: InputCommand, event: Update): Boolean = {
		if command.target eq null then false
		else
			MornyCoeur.extra exec SendSticker(
				event.message.chat.id,
				TelegramStickers ID_404
			).replyToMessageId(event.message.messageId)
			true
	}
	
	def automaticTGListUpdate (): Unit = {
		val listing = commands_toTelegramList
		automaticTGListRemove()
		MornyCoeur.extra exec SetMyCommands(listing:_*)
		logger info
				s"""automatic updated telegram command list :
				   |${commandsTelegramList_toString(listing)}""".stripMargin
	}
	
	def automaticTGListRemove (): Unit = {
		MornyCoeur.extra exec DeleteMyCommands()
		logger info "cleaned up command list"
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
		if (command.aliases ne null) for (alias <- command.aliases)
			if (alias.listed) list += buildOne(alias.name, "", "↑")
		list toArray
	
}
