package cc.sukazyo.cono.morny.test.utils.tgapi.formatting

import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.tagobjects.{Network, Slow}

class TelegramUserInformationTest extends MornyTests with TableDrivenPropertyChecks {
	
	private val examples_telegram_cdn = Table(
		("username", "cdn"),
		("Eyre_S", "cdn5"),
	)
	
	forAll(examples_telegram_cdn) ((username, cdn) => s"while user is @$username :" - {
		
		import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramUserInformation.*
		
		s"datacenter should be $cdn" taggedAs (Slow, Network) in:
			getDataCenterFromUser(username) shouldEqual cdn
		
		"formatted data should as expected" in:
			pending
		
	})
	
}
