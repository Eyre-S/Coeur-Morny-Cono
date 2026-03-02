package cc.sukazyo.cono.morny.system.telegram_api.command

import cc.sukazyo.cono.morny.system.telegram_api.command.name.{CommandName, CommandNameSpec}

trait AbstractCommonCommand extends AbstractCommand with CommandNameSpec {
	
	def names: List[CommandName]
	
	override def isMatch (inputCommand: TInputCommand): Boolean =
		names.exists(_.isMatch(inputCommand.command))
	
}
