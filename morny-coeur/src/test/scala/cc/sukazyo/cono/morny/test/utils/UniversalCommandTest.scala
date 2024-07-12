package cc.sukazyo.cono.morny.test.utils

import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.TableDrivenPropertyChecks

class UniversalCommandTest extends MornyTests with Matchers with TableDrivenPropertyChecks {
	
	"while formatting command from String :" - {
		import cc.sukazyo.cono.morny.system.utils.UniversalCommand as Cmd
		import cc.sukazyo.cono.morny.system.utils.UniversalCommand.Lossy as Lmd
		def whileLossy (info: String): String = "in lossy mode " + info
		def whileStrict (info: String): String = "in strict mode" + info
		
		raw"args should be separated by (\u0020) ascii-space" in:
			Cmd("a b c delta e") shouldEqual Array("a", "b", "c", "delta", "e")
			Lmd("a b c delta e") shouldEqual Array("a", "b", "c", "delta", "e")
		"args should not be separated by non-ascii spaces" in:
			Cmd("tests ダタ　セト") shouldEqual Array("tests", "ダタ　セト")
			Lmd("tests ダタ　セト") shouldEqual Array("tests", "ダタ　セト")
		"multiple ascii-spaces should not generate empty arg in middle" in:
			Cmd("tests    some  of data") shouldEqual Array("tests", "some", "of", "data")
			Lmd("tests    some  of data") shouldEqual Array("tests", "some", "of", "data")
		
		"""texts and ascii-spaces in '' should grouped in one arg""" in:
			Cmd("""tests 'data set'""") shouldEqual Array("tests", "data set")
			Lmd("""tests 'data set'""") shouldEqual Array("tests", "data set")
		"""texts and ascii-spaces in "" should grouped in one arg""" in :
			Cmd("""tests "data  set"""") shouldEqual Array("tests", "data  set")
			Lmd("""tests "data  set"""") shouldEqual Array("tests", "data  set")
		"""texts nested '' should grouped in one arg""" in :
			Cmd("""tests some:'data set'.message is""") shouldEqual Array("tests", "some:data set.message", "is")
			Lmd("""tests some:'data set'.message is""") shouldEqual Array("tests", "some:data set.message", "is")
		"""texts and "" nested '' should grouped in one arg""" in :
			Cmd("""tests "arg1 x":'arg2 y' param""") shouldEqual Array("tests", "arg1 x:arg2 y", "param")
			Lmd("""tests "arg1 x":"arg2 y" param""") shouldEqual Array("tests", "arg1 x:arg2 y", "param")
		"with ' not closed" - {
			whileStrict("should throws IllegalArgumentException") in:
				an[IllegalArgumentException] should be thrownBy Cmd("""use 'it """)
			whileLossy("should be cut at end") in:
				Lmd("use 'it ") shouldEqual Array("use", "it ")
		}
		"""mixed ' and """" - {
			whileStrict("should throws IllegalArgumentsException") in:
				an[IllegalArgumentException] should be thrownBy Cmd("""tests "data set' "of it'""")
			whileLossy("should be seen as a normal character") in:
				Lmd("""tests "data set' "of it'""") shouldEqual Array("tests", "data set' of", "it")
		}
		
		raw"\ should escape itself" in:
			Cmd(raw"input \\data") shouldEqual Array("input", "\\data")
			Lmd(raw"input \\data") shouldEqual Array("input", "\\data")
		raw"\ should escape ascii-space, makes it processed as a normal character" in:
			Cmd(raw"input data\ set") shouldEqual Array("input", "data set")
			Lmd(raw"input data\ set") shouldEqual Array("input", "data set")
		raw"\ should escape ascii-space, makes it can be an arg body" in:
			Cmd(raw"input \  some-thing") shouldEqual Array("input", " ", "some-thing")
			Lmd(raw"input \  some-thing") shouldEqual Array("input", " ", "some-thing")
		raw"""\ should escape "", makes it processed as a normal character""" in :
			Cmd(raw"""use \"inputted""") shouldEqual Array("use", "\"inputted")
			Lmd(raw"""use \"inputted""") shouldEqual Array("use", "\"inputted")
		raw"\ should escape '', makes it processed as a normal character" in:
			Cmd(raw"use \'inputted") shouldEqual Array("use", "'inputted")
			Lmd(raw"use \'inputted") shouldEqual Array("use", "'inputted")
		raw"\ should escape itself which inside a quoted scope" in:
			Cmd(raw"use 'quoted \\ body'") shouldEqual Array("use", "quoted \\ body")
			Lmd(raw"use 'quoted \\ body'") shouldEqual Array("use", "quoted \\ body")
		raw"""\ should escape " which inside a "" scope""" in:
			Cmd(raw"""in "quoted \" body" body""") shouldEqual Array("in", "quoted \" body", "body")
			Lmd(raw"""in "quoted \" body" body""") shouldEqual Array("in", "quoted \" body", "body")
		raw"""\ should escape ' which inside a "" scope""" in :
			Cmd(raw"""in "not-quoted \' body" body""") shouldEqual Array("in", "not-quoted ' body", "body")
			Lmd(raw"""in "not-quoted \' body" body""") shouldEqual Array("in", "not-quoted ' body", "body")
		raw"""\ should escape ' which inside a '' scope""" in :
			Cmd(raw"""in 'quoted \' body' body""") shouldEqual Array("in", "quoted ' body", "body")
			Lmd(raw"""in 'quoted \' body' body""") shouldEqual Array("in", "quoted ' body", "body")
		raw"""\ should escape " which inside a ' scope""" in :
			Cmd(raw"""in 'not-quoted \" body' body""") shouldEqual Array("in", "not-quoted \" body", "body")
			Lmd(raw"""in 'not-quoted \" body' body""") shouldEqual Array("in", "not-quoted \" body", "body")
		raw"\ should not escape ascii-space which inside a quoted scope" in:
			Cmd(raw"""'quoted \ do not escape' did""") shouldEqual Array(raw"quoted \ do not escape", "did")
			Lmd(raw"""'quoted \ do not escape' did""") shouldEqual Array(raw"quoted \ do not escape", "did")
		raw"with \ in the end" - {
			whileStrict("should throws IllegalArgumentException") in:
				an [IllegalArgumentException] should be thrownBy Cmd("something error!\\")
			whileLossy("should seen as normal char") in:
				Lmd("something error!\\") shouldEqual Array("something", "error!\\")
		}
		
		"with multi-line input" - {
			whileStrict("should throws IllegalArgumentException") in:
				an [IllegalArgumentException] should be thrownBy Cmd("something will\nhave a new line")
			whileLossy("should keep new-line char origin like") in:
				Lmd("something will\nhave a new line") shouldEqual Array("something", "will\nhave", "a", "new", "line")
		}
		
		"empty string input should return empty array" in {
			Cmd("") shouldEqual Array.empty[String]
			Lmd("") shouldEqual Array.empty[String]
		}
		
		val example_special_character = Table(
			"char",
			"　",
			"\t",
			"\\t",
			"\\a",
			"/",
			"&&",
			"\\u1234",
		)
		forAll(example_special_character) { char =>
			s"input with special character ($char) should keep origin like" in {
				Cmd(s"$char dataset data[$char]contains parsed") shouldEqual
						Array(char, "dataset", s"data[$char]contains", "parsed")
				Lmd(s"$char dataset data[$char]contains parsed") shouldEqual
					Array(char, "dataset", s"data[$char]contains", "parsed")
			}
		}
	}
	
}
