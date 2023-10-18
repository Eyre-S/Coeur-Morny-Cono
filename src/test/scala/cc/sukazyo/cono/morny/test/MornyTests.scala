package cc.sukazyo.cono.morny.test

import cc.sukazyo.restools.ResourcesPackage
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

abstract class MornyTests extends AnyFreeSpec with should.Matchers {
	
	val assets: ResourcesPackage =
		ResourcesPackage(classOf[MornyTests], "assets_morny_tests")
	
	val pending_val = "[not-implemented]"
	
}
