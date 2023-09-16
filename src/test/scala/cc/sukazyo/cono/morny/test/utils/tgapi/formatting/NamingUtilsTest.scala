package cc.sukazyo.cono.morny.test.utils.tgapi.formatting

import cc.sukazyo.cono.morny.test.MornyTests

class NamingUtilsTest extends MornyTests {
	
	"while generating inline query result id :" - {
		
		"while not use no data :" - {
			
			"(different tag) should return different id" in pending
			"(same tag) should return the same id" in pending
			
		}
		
		"while use data :" - {
			
			"(same tag) with (same data) should return the same id" in pending
			"(same tag) with (different data) should return different id" in pending
			"(different tag) with (same data) should return different id" in pending
			"change tag and data position should return different id" in pending
			
		}
		
	}
	
}
