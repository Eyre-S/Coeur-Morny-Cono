package cc.sukazyo.cono.morny.system.utils.command

class InputCommand protected (
	override val command: String,
	override val args: Array[String],
	override val argsRaw: String
) extends IInputCommand {
	
	def subcommand: Option[InputCommand] = InputCommand(args, argsRaw)
	
}

object InputCommand {
	
	private def apply (args: Array[String], remains: String): Option[InputCommand] = {
		if (args.isEmpty) return None
		Some(new InputCommand(args(0), args.drop(1), remains))
	}
	
	def apply (parser: InputCommandParser = InputCommandParser.Default)(input: String): Option[InputCommand] = {
		val result = parser.parse(input)
		apply(result.args, result.remainsRaw)
	}
	
}
