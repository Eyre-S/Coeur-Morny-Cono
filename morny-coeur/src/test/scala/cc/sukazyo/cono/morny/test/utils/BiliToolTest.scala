package cc.sukazyo.cono.morny.test.utils

import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.util.Random

class BiliToolTest extends MornyTests with TableDrivenPropertyChecks {
	
	private val examples = Table(
		("bv", "av"),
		("17x411w7KC", 170001L),
		("1Q541167Qg", 455017605L),
		("1mK4y1C7Bz", 882584971L),
		("1T24y197V2", 688730800L),
	)
	
	forAll (examples) { (bv, av) => s"while using av$av/BV$bv :" - {
		import cc.sukazyo.cono.morny.social_share.external.bilibili.BiliTool.{toAv, toBv}
		"av to bv works" in { toBv(av) shouldEqual bv }
		"bv to av works" in { toAv(bv) shouldEqual av }
	}}
	
	"BV with unsupported length :" - {
		import cc.sukazyo.cono.morny.social_share.external.bilibili.BiliTool.{toAv, IllegalFormatException}
		val examples = Table(
			"bv",
			"12345",
			"12345678",
			"123456789",
//			"1234567890", length 10 which is supported
			"1234567890a",
			"1234567890ab",
			"1234567890abcdef"
		)
		forAll(examples) { bv =>
			s"length ${bv.length} should throws IllegalFormatException" in:
				an [IllegalFormatException] should be thrownBy toAv(bv)
		}
	}
	
	"BV with special character :" - {
		val examples = Table(
			("bv"        , "contains_special"),
			("1mK4O1C7Bz", "O"),
			("1m04m1C7Bz", "0"),
			("1mK4O1I7Bz", "I"),
			("1mK4O1C7Bl", "l"),
			("1--4O1C7Bl", "[symbols]")
		)
		import cc.sukazyo.cono.morny.social_share.external.bilibili.BiliTool.{toAv, IllegalFormatException}
		forAll(examples) { (bv, with_sp) =>
			s"'$with_sp' should throws IllegalFormatException" in:
				an [IllegalFormatException] should be thrownBy toAv(bv)
		}
	}
	
	"av/bv converting should be reversible" in {
		for (_ <- 1 to 20) {
			val rand_av = Random.between(0, 999999999L)
			import cc.sukazyo.cono.morny.social_share.external.bilibili.BiliTool.{toAv, toBv}
			val my_bv = toBv(rand_av)
			toAv(my_bv) shouldEqual rand_av
			toBv(toAv(my_bv)) shouldEqual my_bv
		}
	}
	
}
