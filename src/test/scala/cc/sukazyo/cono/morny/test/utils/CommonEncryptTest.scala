package cc.sukazyo.cono.morny.test.utils

import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks

class CommonEncryptTest extends MornyTests with TableDrivenPropertyChecks {
	
	"while doing hash :" - {
		
		val examples = Table(
			("md5"                             , "text"),
			("28be57d368b75051da76c068a6733284", "莲子"),
			("9644c5cbae223013228cd528817ba4f5", "莲子\n"),
			("d41d8cd98f00b204e9800998ecf8427e", "")
		)
		
		import cc.sukazyo.cono.morny.util.CommonEncrypt.MD5
		import cc.sukazyo.cono.morny.util.ConvertByteHex.toHex
		forAll (examples) { (md5, text) =>
			s"while hashing text \"$text\" :" - {
				
				s"the MD5 value should be $md5" in { MD5(text).toHex shouldEqual md5 }
				
				"other algorithms" in pending
				
			}
		}
		
		s"while hashing binary file $pending_val" in pending
		
	}
	
}
