package cc.sukazyo.cono.morny.system.telegram_api.command.name

class HiddenName (name: String) extends CommonName (name) {
	
	override def isListed: Boolean = false
	
}
