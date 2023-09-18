package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update

trait ISimpleCommand {
	
	val name: String
	val aliases: Array[ICommandAlias]|Null
	
	def execute (using command: InputCommand, event: Update): Unit
	
}
