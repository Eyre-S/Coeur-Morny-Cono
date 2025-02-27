package cc.sukazyo.cono.morny.system.telegram_api.command

import cc.sukazyo.cono.morny.system.MornySystemTests

class TestInputCommand extends MornySystemTests {
	
	"On creating InputCommand" - {
		
		"using the basic constructor without target field, " - {
			
			"constructor should be able to receive string array" in {
				// language=scala 3
				"""InputCommand(Array("test", "arg1", "arg2"))
				  |""".stripMargin should compile
			}
			
			"constructor should be able to receive one string" in {
				// language=scala 3
				"""InputCommand("test arg1 arg2")
				  |""".stripMargin should compile
			}
			
			"the first args should be the command name" in {
				val command = InputCommand(Array("test", "arg1", "arg2"))
				command.command shouldEqual "test"
			}
			
			"the rest args should be the command args" in {
				val command = InputCommand(Array("test", "arg1", "arg2"))
				command.args shouldEqual Array("arg1", "arg2")
			}
			
			"the command args should be an empty array if there is no args" in {
				val command = InputCommand(Array("test"))
				command.args shouldEqual Array.empty[String]
			}
			
			"the target should always be null" in {
				val command = InputCommand(Array("test", "arg1", "arg2"))
				command.target shouldBe null
				val command2 = InputCommand("test")
				command2.target shouldBe null
			}
			
			"when receiving one single string, it should be split using UniversalCommand format" in {
				val command = InputCommand("test arg1 arg2")
				command.command shouldEqual "test"
				command.args shouldEqual Array("arg1", "arg2")
				val command2 = InputCommand("""'test a' 'something'""")
				command2.command shouldEqual "test a"
				command2.args shouldEqual Array("something")
			}
			
			"the prefix '/' should not be removed" in {
				val command = InputCommand("/test arg1 arg2")
				command.command shouldEqual "/test"
			}
			
			"the '@' symbol in the first args should not be processed as target separator" in {
				val command = InputCommand(Array("test@something", "arg1", "arg2"))
				command.command shouldEqual "test@something"
				command.target shouldBe null
			}
			
			def blanks (ic: InputCommand) = {
				"the command name should be blank string" in {
					ic.command shouldEqual ""
				}
			}
			
			"when using empty array to constructor" - {
				blanks(InputCommand(Array.empty[String]))
			}
			
			"when using blank string to constructor" - {
				blanks(InputCommand(""))
			}
			
		}
		
		"using the basic constructor with target field, " - {
			
			"the target should be the second parameter" in {
				val command = InputCommand(Array("test", "arg1", "arg2"), "target")
				command.target shouldEqual "target"
				val command2 = InputCommand("command something", "target name")
				command2.target shouldEqual "target name"
			}
			
			"the first parameter should be processed like the basic constructor" in {
				val command1 = InputCommand(Array("test", "arg1", "arg2"), "target")
				command1.command shouldEqual "test"
				command1.args shouldEqual Array("arg1", "arg2")
				val command2 = InputCommand("command something", "target name")
				command2.command shouldEqual "command"
				command2.args shouldEqual Array("something")
			}
			
		}
		
		"using the telegram constructor, " - {
			
			"the string formatted command should be accepted" in {
				// language=scala 3
				"""InputCommand.inTelegram("test arg1 arg2")
				  |""".stripMargin should compile
			}
			
			"the string array formatted command should be accepted" in {
				// language=scala 3
				"""InputCommand.inTelegram(Array("test", "arg1", "arg2"))
				  |""".stripMargin should compile
			}
			
			"the command should be prased like basic constructor" in {
				val command = InputCommand.inTelegram("test arg1 arg2")
				command.command shouldEqual "test"
				command.args shouldEqual Array("arg1", "arg2")
				val command2 = InputCommand.inTelegram(Array("test", "arg1", "arg2"))
				command2.command shouldEqual "test"
				command2.args shouldEqual Array("arg1", "arg2")
			}
			
			"there should be no second parameter as manually defined target" in {
				// language=scala 3
				"""InputCommand.inTelegram("test arg1 arg2", "target")
				  |""".stripMargin shouldNot compile
			}
			
			"the prefix '/' should not be removed" in {
				val command = InputCommand.inTelegram("/test arg1 arg2")
				command.command shouldEqual "/test"
			}
			
			"the '@' symbol in the first args should be processed as target separator" in {
				val command = InputCommand.inTelegram(Array("test@something", "arg1", "arg2"))
				command.command shouldEqual "test"
				command.target shouldEqual "something"
			}
			
			"only first '@' symbol should be processed as target separator" in {
				val command = InputCommand.inTelegram(Array("test@something@else", "arg1", "arg2"))
				command.command shouldEqual "test"
				command.target shouldBe "something@else"
			}
			
		}
		
	}
	
}
