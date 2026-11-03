package cc.sukazyo.cono.morny.test.utils.tgapi

import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import org.scalatest.prop.TableDrivenPropertyChecks

class InputCommandTest extends MornyTests with TableDrivenPropertyChecks {
	
	"while create new InputCommand :" - {
		
		val examples = Table[String, String, String|Null, Array[String], String](
			(
				"source",
				"command",
				"target",
				"args",
				"data"
			),
			(
				"exit@sukazyo_deving_bot",
				"exit",
				"sukazyo_deving_bot",
				Array.empty[String],
				""
			),
			(
				"test@a@b",
				"test",
				"a@b",
				Array.empty[String],
				""
			),
			(
				"test-data@random#user",
				"test-data",
				"random#user",
				Array.empty[String],
				""
			),
			(
				"info@sukazyo_deving_bot stickers.ID_403",
				"info",
				"sukazyo_deving_bot",
				Array("stickers.ID_403"),
				""
			),
			(
				"info some extra info",
				"info",
				null,
				Array("some", "extra", "info"),
				""
			),
			(
				"",
				"",
				null,
				Array.empty[String],
				""
			),
			(
				"enc@bot based\nSome text",
				"enc",
				"bot",
				Array("based"),
				"Some text"
			),
			(
				"hi\nMultiple lines...\n - data!",
				"hi",
				null,
				Array.empty[String],
				"Multiple lines...\n - data!"
			)
		)
		
		examples forEvery { (source, command, target, args, data) =>
			
			s"while input is $source:" - {
				
				def _ic: InputCommand = InputCommand(source)
				
				s"command should be '$command'" in { _ic.command shouldEqual command }
				s"target should be '$target'" in {_ic.target shouldEqual target}
				
				"args array should always exists" in { _ic.args shouldNot equal (null) }
				s"args should parsed to array ${args.mkString}" in { _ic.args shouldEqual args }
				"data that keeps after first lines should be kept" in { _ic.data shouldEqual data }
				
			}
			
		}
		
	}
	
}
