package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update

class MornyInformationOlds (using base: MornyInformation) {
	
	object Version extends ISimpleCommand:
		override val name: String = "version"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit = base.echoVersion
	
	object Runtime extends ISimpleCommand:
		override val name: String = "runtime"
		override val aliases: List[ICommandAlias] = Nil
		override def execute (using command: InputCommand, event: Update): Unit = base.echoRuntime
	
}