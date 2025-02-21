package cc.sukazyo.cono.morny.system.telegram_api.command

import cc.sukazyo.cono.morny.system.utils.UniversalCommand

class InputCommand private (
		val target: String|Null,
		val command: String,
		val args: Array[String]
) {
	
	override def toString: String =
//		s"{{$command}@{$target}#{${args.mkString}}"
		s"/$command${if target != null then s"@$target" else ""} ${args.mkString(" ")}"
	
	def subcommand: InputCommand =
		InputCommand(args, target)
	
}

object InputCommand {
	
	private final val TARGET_DEFAULT: String|Null = null
	
	def apply (input: Array[String], target: String|Null): InputCommand = {
		new InputCommand(
			target,
			input.headOption.getOrElse(""),
			input drop 1
		)
	}
	
	def apply (input: Array[String]): InputCommand =
		InputCommand(input, TARGET_DEFAULT)
	
	def apply (input: String): InputCommand =
		InputCommand(UniversalCommand.Lossy(input))
	
	def apply (input: String, target: String|Null): InputCommand =
		InputCommand(UniversalCommand.Lossy(input), target)
	
	def inTelegram (input: String): InputCommand = {
		inTelegram(UniversalCommand.Lossy(input))
	}
	
	def inTelegram (input: Array[String]): InputCommand = {
		val _ex = if input.nonEmpty then input(0).split("@", 2) else Array.empty[String]
		val _args = input drop 1
		new InputCommand(
			if _ex.length > 1 then _ex(1) else null,
			if _ex.nonEmpty then _ex(0) else "",
			_args
		)
	}
	
}
