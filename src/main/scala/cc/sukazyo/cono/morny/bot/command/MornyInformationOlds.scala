package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update

object MornyInformationOlds {
	
	object Version extends ISimpleCommand:
		override val name: String = "version"
		override val aliases: Array[ICommandAlias] | Null = null
		override def execute (using command: InputCommand, event: Update): Unit = MornyInformation.echoVersion
	
	object Runtime extends ISimpleCommand:
		override val name: String = "runtime"
		override val aliases: Array[ICommandAlias] | Null = null
		override def execute (using command: InputCommand, event: Update): Unit = MornyInformation.echoRuntime
	
}
