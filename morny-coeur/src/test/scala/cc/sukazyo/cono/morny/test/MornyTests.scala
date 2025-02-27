package cc.sukazyo.cono.morny.test

import cc.sukazyo.restools.{ResourceDirectory, ResourcePackage}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should

abstract class MornyTests extends AnyFreeSpec with should.Matchers {
	
	val pack: ResourcePackage = ResourcePackage.get("assets_morny_tests")
	val assets: ResourceDirectory = pack.getDirectory("assets_morny_tests")
	
}
