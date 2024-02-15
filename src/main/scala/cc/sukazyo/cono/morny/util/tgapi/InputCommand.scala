package cc.sukazyo.cono.morny.util.tgapi

import cc.sukazyo.cono.morny.util.UniversalCommand

class InputCommand private (
		val target: String|Null,
		val command: String,
		val args: Array[String]
) {
	
	override def toString: String =
		s"{{$command}@{$target}#{${args.mkString}}"
	
}

object InputCommand {
	
	def apply (input: Array[String]): InputCommand = {
		val _ex = if input.nonEmpty then input(0).split("@", 2) else Array.empty[String]
		val _args = input drop 1
		new InputCommand(
			if _ex.length > 1 then _ex(1) else null,
			if _ex.nonEmpty then _ex(0) else "",
			_args
		)
	}
	
	//noinspection NoTailRecursionAnnotation
	def apply (input: String): InputCommand =
		InputCommand(UniversalCommand.Lossy(input))
	
}
