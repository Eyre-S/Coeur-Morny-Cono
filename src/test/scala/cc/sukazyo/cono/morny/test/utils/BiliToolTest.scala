package cc.sukazyo.cono.morny.test.utils

import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks

import scala.util.Random

class BiliToolTest extends MornyTests with TableDrivenPropertyChecks {
	
	private val examples = Table(
		(    "bv id"   ,      "av id"     ),
		( "17x411w7KC" ,          170001L ),
		( "1Q541167Qg" ,       455017605L ),
		( "1mK4y1C7Bz" ,       882584971L ),
		( "1T24y197V2" ,       688730800L ),
		( "1b2421A7FH" ,      1600345142L ),
		( "1DB421k7zX" ,      1350018000L ),
		( "19m411D7wx" ,      1900737470L ),
		( "1LQ4y1A7im" ,       709042411L ),
		( "1L9Uoa9EUx" , 111298867365120L ),
		// from #54 (https://github.com/Eyre-S/Coeur-Morny-Cono/issues/54)
		( "1mS411K7Ba" ,      1905492274L ),
		( "1mgt8edE43" , 113150779333900L )
	)
	
	forAll (examples) { (bv, av) => s"while using av$av/BV$bv :" - {
		import cc.sukazyo.cono.morny.util.BiliTool.{toAv, toBv}
		"av to bv works" in { toBv(av) shouldEqual bv }
		"bv to av works" in { toAv(bv) shouldEqual av }
	}}
	
	"BV with unsupported length :" - {
		import cc.sukazyo.cono.morny.util.BiliTool.{toAv, IllegalBVFormatException}
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
				an [IllegalBVFormatException] should be thrownBy toAv(bv)
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
		import cc.sukazyo.cono.morny.util.BiliTool.{toAv, IllegalBVFormatException}
		forAll(examples) { (bv, with_sp) =>
			s"BV id with '$with_sp' should throws IllegalBVFormatException" in:
				an [IllegalBVFormatException] should be thrownBy toAv(bv)
		}
	}
	
	"BV id must started with `1` in current version" in {
		import cc.sukazyo.cono.morny.util.BiliTool.{toAv, IllegalBVFormatException}
		an [IllegalBVFormatException] should be thrownBy toAv("2mK4y1C7Bz")
	}
	
	"AV id must not smaller that `1`" in {
		import cc.sukazyo.cono.morny.util.BiliTool.{toBv, IllegalAVFormatException}
		an [IllegalAVFormatException] should be thrownBy toBv(0)
		an [IllegalAVFormatException] should be thrownBy toBv(-826624291)
		an [IllegalAVFormatException] should be thrownBy toBv(-296798903L)
	}
	
	s"AV id must not bigger that 2^51 (or ${1L << 51})" in {
		import cc.sukazyo.cono.morny.util.BiliTool.{toBv, IllegalAVFormatException}
		an [IllegalAVFormatException] should (not be thrownBy( toBv(1L << 51) ))
		an [IllegalAVFormatException] should be thrownBy toBv((1L << 51) + 1)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 52)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 53)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 54)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 55)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 56)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 57)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 58)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 59)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 60)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 61)
		an [IllegalAVFormatException] should be thrownBy toBv(1L << 62)
	}
	
	"av/bv converting should be reversible" in {
		for (_ <- 1 to 20) {
			val rand_av = Random.between(1, (1L<<51)+1)
			import cc.sukazyo.cono.morny.util.BiliTool.{toAv, toBv}
			val my_bv = toBv(rand_av)
			toAv(my_bv) shouldEqual rand_av
			toBv(toAv(my_bv)) shouldEqual my_bv
		}
	}
	
}
