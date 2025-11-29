package cc.sukazyo.cono.morny.system.utils.command

class InputCommand private (
	val command: String,
	val args: Array[String],
	val argsRaw: String
) {
	
	def nextLevel: Option[InputCommand] = InputCommand(args, argsRaw)
	
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
