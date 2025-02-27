package cc.sukazyo.cono.morny.system.utils

import cc.sukazyo.cono.morny.system.MornySystemTests
import cc.sukazyo.cono.morny.system.utils.CommonEncrypt.{MD5, SHA1, SHA256, SHA512}
import cc.sukazyo.cono.morny.system.utils.ConvertByteHex.toHex

class TestCommonEncrypt extends MornySystemTests {
	
	"while doing hash :" - {
		
		val examples = Table(
			(
				"text",
				"md5",
				"sha1",
				"sha256",
				"sha512"
			),
			(
				"莲子",
				"28be57d368b75051da76c068a6733284",
				"556f7cadfcbffdcbf41b4aa1843528b4406e62e6",
				"f7d6f172cb4a8d1a8f7513ee94c7b6bbf1cf5c8c643182ebbb1b9ab472704e9b",
				"4be6cf5dc44582d913d4d716ef4528f04e08d94a45eee0f27395003d3c83be535d5b9a40f510625bc6fee3481f6a1de600057ceff6488c5953f6172641f4768d"
			),
			(
				"莲子\n",
				"9644c5cbae223013228cd528817ba4f5",
				"86329fc40e4bab2c410e35ddbec7ab8a7b6574d6",
				"3372ca6821832bebd681c705862c01d137cba9cf288f95465ee7876affc90ba0",
				"bedf1c61330c0f945fa4f84aaccf2778b5c77926916689e8b8e4c3d1d567dc8b9d91643ff3451365e4fd04f789ca229e0b2ca61dd4976ad6f866f5600617430c"
			),
			(
				"",
				"d41d8cd98f00b204e9800998ecf8427e",
				"da39a3ee5e6b4b0d3255bfef95601890afd80709",
				"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
				"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"
			)
		)
		
		forAll (examples) { (text, md5, sha1, sha256, sha512) =>
			s"while hashing text \"$text\" :" - {
				
				s"the MD5 value should be $md5" in { MD5(text).toHex shouldEqual md5 }
				s"the SHA1 should be $sha1" in { SHA1(text).toHex shouldEqual sha1 }
				s"the SHA256 should be $sha256" in { SHA256(text).toHex shouldEqual sha256 }
				s"the SHA512 should be $sha512" in { SHA512(text).toHex shouldEqual sha512 }
				
			}
		}
		
		case class ExampleHashValue (
			md5: String,
			sha1: String,
			sha256: String,
			sha512: String
		)
		val examples_binary = Table[String|Null, ExampleHashValue](
			("file", "hashes"),
			(
				null, ExampleHashValue(
				"d41d8cd98f00b204e9800998ecf8427e",
				"da39a3ee5e6b4b0d3255bfef95601890afd80709",
				"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
				"cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e"
			)),
			(
				"md5.gif", ExampleHashValue(
				"f5ca4f935d44b85c431a8bf788c0eaca",
				"784cefac7a3699d704756d1a04189d6157405906",
				"c0aa75d5345efae1019ca7a56eabc8673499dcee8ab8a8d657fb69f1f929b909",
				"2d3123c543aa1745eeae57d2e6c31b6ea07dd2bb14cef2b5939116f9cf705953fb43cf6162c87ee1c7175d1de4af6d9de6f2bc817065cc854b912877848f937b"
			))
		)
		
		forAll(examples_binary) { (file, hashes) =>
			val _name = if file == null then "empty file" else s"file $file"
			val _data =
				if file == null then
					Array.empty[Byte]
				else
					Assets.root.getFile(file).read.readAllBytes
			
			s"while hashing binary $_name :" - {
				
				s"the MD5 value should be ${hashes.md5}" in { MD5(_data).toHex shouldEqual hashes.md5 }
				s"the SHA1 should be $hashes.sha1" in { SHA1(_data).toHex shouldEqual hashes.sha1 }
				s"the SHA256 should be $hashes.sha256" in { SHA256(_data).toHex shouldEqual hashes.sha256 }
				s"the SHA512 should be $hashes.sha512" in { SHA512(_data).toHex shouldEqual hashes.sha512 }
				
			}
			
		}
		
	}
	
}
