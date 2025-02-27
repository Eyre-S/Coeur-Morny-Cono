package cc.sukazyo.cono.morny.system.telegram_api.formatting

import cc.sukazyo.cono.morny.system.MornySystemTests

class TestNamingUtils extends MornySystemTests {
	
	"while generating inline query result id :" - {
		
		import NamingUtils.inlineQueryId
		
		"while not use no data :" - {
			
			"(different tag) should return different id" in:
				inlineQueryId("abc") should not equal inlineQueryId("abd")
				inlineQueryId("abc") should not equal inlineQueryId("abe")
			"(same tag) should return the same id" in:
				inlineQueryId("abc") shouldEqual inlineQueryId("abc")
				inlineQueryId("[e]vo]wvr'L\"pno[irvP)v]") shouldEqual inlineQueryId("[e]vo]wvr'L\"pno[irvP)v]")
			
		}
		
		"while use data :" - {
			
			"(same tag) with (same data) should return the same id" in:
				inlineQueryId("random-tag", "123456789") shouldEqual
					inlineQueryId("random-tag", "123456789")
			"(same tag) with (different data) should return different id" in:
				inlineQueryId("random-tag", "123456789") should not equal
					inlineQueryId("random-tag", "987654321")
			"(different tag) with (same data) should return different id" in:
				inlineQueryId("random-tag", "123456789") should not equal
					inlineQueryId("set-tag", "123456789")
			"change tag and data position should return different id" in:
				inlineQueryId("tag", "data") should not equal
					inlineQueryId("data", "tag")
			
		}
		
	}
	
}
