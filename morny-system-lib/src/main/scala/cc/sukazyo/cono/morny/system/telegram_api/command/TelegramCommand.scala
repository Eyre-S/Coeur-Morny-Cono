package cc.sukazyo.cono.morny.system.telegram_api.command

import cc.sukazyo.cono.morny.system.telegram_api.command.name.CommonName

trait TelegramCommand extends SimpleCommand with AbstractTelegramCommand {
	
	/** The param rule of this command, used in human-readable command list.
	  *
	  * The param rule uses a symbol language to describe how this command
	  * receives paras.
	  *
	  * Set it empty to make this scope not available.
	  */
	def paramRule: String
	
	/** The description/introduction of this command, used in human-readable
	  * command list.
	  */
	def description: String
	
	override def telegramInfos: List[TCommandInfo] = {
		val requiredNames = this.names
			.filter(_.isInstanceOf[CommonName])
			.filter(_.isListed)
			.asInstanceOf[List[CommonName]]
		if requiredNames.isEmpty then Nil
		else {
			val head = TCommandInfo(requiredNames.head.name, this.paramRule + " - " + this.description)
			val aliases = requiredNames.drop(1).map(x => TCommandInfo(x.name, "↑"))
			head :: aliases
		}
	}
	
}
