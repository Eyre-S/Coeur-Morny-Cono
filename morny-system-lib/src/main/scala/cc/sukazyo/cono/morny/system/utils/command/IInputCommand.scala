package cc.sukazyo.cono.morny.system.utils.command

trait IInputCommand {
	
	val command: String
	val args: Array[String]
	val argsRaw: String
	
	def subcommand: Option[IInputCommand]
	
}
