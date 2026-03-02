package cc.sukazyo.cono.morny.system.telegram_api.command.name

trait CommandName {
	
	def isMatch (input: String): Boolean
	
	def isListed: Boolean
	
}
