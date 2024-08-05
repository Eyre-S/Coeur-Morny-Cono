package cc.sukazyo.cono.morny.test

import cc.sukazyo.restools.{ResourceDirectory, ResourcePackage}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

abstract class MornyTests extends AnyFreeSpec with should.Matchers {
	
	val assets: ResourceDirectory =
		ResourcePackage.get("assets_morny_tests").getDirectory("assets_morny_tests")
	
	val pending_val = "[not-implemented]"
	
}
