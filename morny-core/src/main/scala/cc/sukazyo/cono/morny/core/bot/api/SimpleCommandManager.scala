package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.bot.api.MornyCommandManager.CommandMap
import cc.sukazyo.cono.morny.system.telegram_api.command.{InputCommand, ISimpleCommand}
import com.pengrad.telegrambot.model.Update

import scala.collection.mutable

/** The most basic command manager for Telegram commands.
  * 
  * This manager contains numbers [[ISimpleCommand]], also provides methods to register them.
  * Also, it provides a method to execute a command.
  * 
  * If the command is not found, this manager will do nothing in default.
  * You can customize the behavior by overriding the method [[nonCommandExecutable]].
  */
trait SimpleCommandManager {
	
	protected val commands: CommandMap = mutable.SeqMap.empty
	
	protected def doRegister [T <: ISimpleCommand](it: T): Unit =
		this.commands += (it.name -> it)
		for (alias <- it.aliases)
			this.commands += (alias.name -> it)
	
	infix def register [T <: ISimpleCommand] (command: T): Unit =
		doRegister(command)
	def register [T <: ISimpleCommand] (commands: T*): Unit =
		for (command <- commands) doRegister(command)
	
	def emitCommands (using command: InputCommand, event: Update): Boolean = {
		if (commands contains command.command)
			commands(command.command) execute;
			true
		else nonCommandExecutable
	}
	
	protected def nonCommandExecutable (using command: InputCommand, event: Update): Boolean = {
		false
	}
	
}

object SimpleCommandManager {
	def apply (): SimpleCommandManager = new SimpleCommandManager {}
}
