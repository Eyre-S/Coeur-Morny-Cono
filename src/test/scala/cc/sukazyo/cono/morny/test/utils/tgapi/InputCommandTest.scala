package cc.sukazyo.cono.morny.test.utils.tgapi

import cc.sukazyo.cono.morny.test.MornyTests

class InputCommandTest extends MornyTests {
	
	"while create new InputCommand :" - {
		
		s"while input is $pending_val:" - {
			
			s"command should be $pending_val" in pending
			s"target should be $pending_val" in pending
			
			"args array should always exists" in pending
			
			s"args should parsed to array $pending_val" in pending
			
		}
		
	}
	
}
