package cc.sukazyo.cono.morny.system.telegram_api.command

import cc.sukazyo.cono.morny.system.utils.command.{InputCommand, InputCommandParser}

class TGInputCommand protected (
	_command: String,
	val target: String|Null,
	_args: Array[String],
	_argsRaw: String
) extends InputCommand (_command, _args, _argsRaw) {
	
	override def toString: String =
		//s"{{$command}@{$target}#{${args.mkString}}"
		s"/$command${if target != null then s"@$target" else ""} ${args.mkString(" ")}"
	
}

object TGInputCommand {

	def apply (parser: InputCommandParser = InputCommandParser.Default)(input: String): Option[TGInputCommand] = {
		val parsed = parser.parse(input)
		if parsed.args.isEmpty then
			return None
		val _first = parsed.args(0).split("@", 2)
		Some(new TGInputCommand(
			_first.head,
			_first.lift(1).orNull,
			parsed.args.drop(1),
			parsed.remainsRaw
		))
	}

}
