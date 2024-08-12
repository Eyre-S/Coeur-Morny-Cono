package cc.sukazyo.cono.morny.test

import cc.sukazyo.restools.{ResourceDirectory, ResourcePackage}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

object MornyTests extends MornyTests.Assets with MornyTests.Keywords {
	
	trait Assets {
		val assetsPackage: ResourcePackage = ResourcePackage.get("assets_morny_tests")
		val assets: ResourceDirectory = assetsPackage.getDirectory("assets_morny_tests")
	}
	
	trait Keywords {
		val pending_val = "[not-implemented]"
	}
	
}

abstract class MornyTests
	extends AnyFreeSpec
	with should.Matchers
	with MornyTests.Assets
	with MornyTests.Keywords
