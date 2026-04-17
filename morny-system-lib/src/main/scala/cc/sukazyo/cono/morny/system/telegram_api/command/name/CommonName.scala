package cc.sukazyo.cono.morny.system.telegram_api.command.name

class CommonName (val name: String) extends CommandName {
	
	override def isMatch (input: String): Boolean =
		name == input
	
	override def isListed: Boolean = true
	
}
