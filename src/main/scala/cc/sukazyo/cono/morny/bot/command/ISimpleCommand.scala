package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update

/** A simple command.
  *
  * Contains only [[name]] and [[aliases]].
  *
  * Won't be listed to end-user. if you want the command listed,
  * see [[ITelegramCommand]].
  *
  */
trait ISimpleCommand {
	
	/** the main name of the command.
	  *
	  * must have a value as the unique identifier of this command.
	  */
	val name: String
	/** aliases of the command.
	  *
	  * Alias means it is the same to call [[name main name]] when call this.
	  * There can be multiple aliases. But notice that, although alias is not
	  * the unique identifier, it uses the same namespace with [[name]], means
	  * it also cannot be duplicate with other [[name]] or [[aliases]].
	  *
	  * It can be [[Null]], means no aliases.
	  */
	val aliases: Array[ICommandAlias]|Null
	
	/** The work code of this command.
	  *
	  * @param command The parsed input command which called this command.
	  * @param event The raw event which called this command.
	  */
	def execute (using command: InputCommand, event: Update): Unit
	
}
