package cc.sukazyo.cono.morny.system.telegram_api.formatting

import cc.sukazyo.cono.morny.system.MornySystemTests
import org.scalatest.tagobjects.{Network, Slow}

class TestTelegramUserInformation extends MornySystemTests {
	
	private val examples_telegram_cdn = Table(
		("username", "cdn"),
		("Eyre_S", "cdn5"),
		("ankarinnie", "cdn1")
	)
	
	forAll(examples_telegram_cdn) ((username, cdn) => s"while user is @$username :" - {
		
		import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramUserInformation.*
		
		s"datacenter should be $cdn" taggedAs (Slow, Network) in:
			getDataCenterFromUser(username)(using Sttp.testingBasicRequest) shouldEqual cdn
		
		"formatted data should as expected" in:
			pending
		
	})
	
}
