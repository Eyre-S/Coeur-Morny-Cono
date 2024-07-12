package cc.sukazyo.cono.morny.test.utils

import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks

class ConvertByteHexTest extends MornyTests with TableDrivenPropertyChecks {
	
	private val examples_hex = Table(
		("byte"      , "hex"),
		( 0x00 toByte, "00"),
		( 0x01 toByte, "01"),
		( 0x20 toByte, "20"),
		( 0x77 toByte, "77"),
		(-0x60 toByte, "a0"),
		( 0x0a toByte, "0a"),
		(-0x01 toByte, "ff"),
		( 0xfb toByte, "fb"),
	)
	
	"while using Byte#toHex :" - forAll (examples_hex) ((byte, hex) => {
		s"byte ($byte) should be hex '$hex''" in {
			import cc.sukazyo.cono.morny.system.utils.ConvertByteHex.toHex
			(byte toHex) shouldEqual hex
		}
	})
	
	private val examples_hexs = Table(
		("bytes", "hex"),
		(Array[Byte](0x00), "00"),
		(Array[Byte](0xff toByte), "ff"),
		(Array[Byte](0xc3 toByte), "c3"),
		(Array[Byte](), ""),
		(Array[Byte](0x30,0x0a,0x00,0x04,0xb0.toByte,0x00), "300a0004b000"),
		(Array[Byte](0x00,0x00,0x0a,0xff.toByte,0xfc.toByte,0xab.toByte,0x00.toByte,0x04), "00000afffcab0004"),
		(Array[Byte](0x00,0x7c,0x11,0x28,0x88.toByte,0xa6.toByte,0xfc.toByte,0x30), "007c112888a6fc30"),
	)
	
	"while using Array[Byte]#toHex :" - forAll(examples_hexs) ((bytes, hex) => {
		s"byte array(${bytes mkString ","}) should be hex string $hex" in {
			import cc.sukazyo.cono.morny.system.utils.ConvertByteHex.toHex
			(bytes toHex) shouldEqual hex
		}
	})
	
}
