package cc.sukazyo.cono.morny.test.utils.tgapi

import cc.sukazyo.cono.morny.system.telegram_api.command.InputCommand
import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks

class InputCommandTest extends MornyTests with TableDrivenPropertyChecks {
	
	"while create new InputCommand :" - {
		
		val examples = Table[String|Array[String], String, String|Null, Array[String]](
			(
				"source",
				"command",
				"target",
				"args"
			),
			(
				"exit@sukazyo_deving_bot",
				"exit",
				"sukazyo_deving_bot",
				Array.empty[String]
			),
			(
				"test@a@b",
				"test",
				"a@b",
				Array.empty[String]
			),
			(
				"test-data@random#user",
				"test-data",
				"random#user",
				Array.empty[String]
			),
			(
				"info@sukazyo_deving_bot stickers.ID_403",
				"info",
				"sukazyo_deving_bot",
				Array("stickers.ID_403")
			),
			(
				"info some extra info",
				"info",
				null,
				Array("some", "extra", "info")
			),
			(
				"",
				"",
				null,
				Array.empty[String]
			)
		)
		
		examples forEvery { (source, command, target, args) =>
			
			val _source_describe = source match
				case s: String => s
				case r: Array[String] => r.mkString
			s"while input is $_source_describe:" - {
				
				val _ic: InputCommand = source match
					case s: String => InputCommand(s)
					case r: Array[String] => InputCommand(r)
				
				s"command should be '$command'" in { _ic.command shouldEqual command }
				s"target should be '$target'" in {_ic.target shouldEqual target}
				
				"args array should always exists" in { _ic.args shouldNot equal (null) }
				s"args should parsed to array ${args.mkString}" in { _ic.args shouldEqual args }
				
			}
			
		}
		
	}
	
}
