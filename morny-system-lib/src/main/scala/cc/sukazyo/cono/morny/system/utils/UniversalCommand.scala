package cc.sukazyo.cono.morny.system.utils

import cc.sukazyo.cono.morny.system.utils.command.InputCommandParser

/**
  * @todo docs
  * @todo maybe there can have some encapsulation
  */
object UniversalCommand  {
	
	opaque type StrictMode = Boolean
	//noinspection ScalaWeakerAccess
	val strict: StrictMode = true
	//noinspection ScalaWeakerAccess
	val lossy: StrictMode = false
	
	def apply (using strict: StrictMode = strict)(input: String): Array[String] = 
		(
			if strict then InputCommandParser.ClassicStrictParser
			else InputCommandParser.ClassicLossyParser
		).parse(input).args
	
	object Lossy:
		def apply (input: String): Array[String] = UniversalCommand(using lossy)(input)
	
}
