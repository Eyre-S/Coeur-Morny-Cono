package cc.sukazyo.cono.morny.test.data

import cc.sukazyo.cono.morny.data.BilibiliForms.*
import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.tagobjects.{Network, Slow}

class BilibiliFormsTest extends MornyTests with TableDrivenPropertyChecks {
	
	"while parsing bilibili video link :" - {
		
		"raw avXXX should be parsed" in:
			parse_videoUrl("av455017605") shouldEqual BiliVideoId(455017605L, "1Q541167Qg")
		"raw BVXXX should be parsed" in:
			parse_videoUrl("BV1T24y197V2") shouldEqual BiliVideoId(688730800L, "1T24y197V2")
		"raw id without av/BV prefix should not be parsed" in:
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("1T24y197V2")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("455017605")
		"av/bv prefix can be either uppercase or lowercase" in:
			parse_videoUrl("bv1T24y197V2") shouldEqual BiliVideoId(688730800L, "1T24y197V2")
			parse_videoUrl("AV455017605") shouldEqual BiliVideoId(455017605L, "1Q541167Qg")
		
		"av/bv bilibili.com link should be parsed" in:
			parse_videoUrl("https://www.bilibili.com/video/AV455017605") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
			parse_videoUrl("https://www.bilibili.com/video/bv1T24y197V2") shouldEqual
				BiliVideoId(688730800L, "1T24y197V2")
		"bilibili.com link can have protocol http:// or https://" in:
			parse_videoUrl("http://www.bilibili.com/video/AV455017605") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
		"bilibili.com link can omit protocol http or https" in :
			parse_videoUrl("www.bilibili.com/video/AV455017605") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
		"bilibili.com link can omit www. prefix" in :
			parse_videoUrl("bilibili.com/video/AV455017605") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
			parse_videoUrl("https://bilibili.com/video/AV455017605") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
		"bilibili.com link can be search result link (with /s path prefix)" in :
			parse_videoUrl("bilibili.com/s/video/AV455017605") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
			parse_videoUrl("https://www.bilibili.com/s/video/AV455017605") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
		"bilibili.com link can only be video link" in :
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("bilibili.com/s/media/AV455017605")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://www.bilibili.com/media/AV455017605")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://www.bilibili.com/AV455017605")
		"bilibili.com link can take parameters" in :
			parse_videoUrl("https://www.bilibili.com/video/av455017605?vd_source=123456") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
			parse_videoUrl("bilibili.com/video/AV455017605?mid=12hdowhAID82EQ&289EHD8AHDOIWU8=r2aur9%3Bi0%3AJ%7BRQJH%28QJ.%5BropWG%3AKR%24%28O%7BGR") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg")
		"video part within bilibili.com link params should be parsed" in :
			parse_videoUrl("https://www.bilibili.com/video/BV1Q541167Qg?p=1") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg", 1)
			parse_videoUrl("https://www.bilibili.com/video/av455017605?p=1&vd_source=123456") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg", 1)
			parse_videoUrl("bilibili.com/video/AV455017605?mid=12hdowhAI&p=5&x=D82EQ&289EHD8AHDOIWU8=r2aur9%3Bi0%3AJ%7BRQJH%28QJ.%5BropWG%3AKR%24%28O%7BGR") shouldEqual
				BiliVideoId(455017605L, "1Q541167Qg", 5)
		
		"av id with more than 12 digits should not be parsed" in :
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("av4550176087554")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("bilibili.com/video/av4550176087554")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("av455017608755634345565341256")
		"av id with 0 digits should not be parsed" in :
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("av")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("bilibili.com/video/av")
		"BV id with not 10 digits should not be parsed" in :
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("BV123456789")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("BV12345678")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("bilibili.com/video/BV12345678901")
		
		"url which is not bilibili link should not be parsed" in:
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://www.pilipili.com/video/av123456")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://pilipili.com/video/av123456")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://blilblil.com/video/av123456")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://bilibili.cc/video/av123456")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://vxbilibili.com/video/av123456")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://bilibiliexc.com/video/av123456")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("C# does not have type erasure. C# has actual generic types deeply baked into the runtime.\n\n好文明")
		
		"url which is a b23 video link should be parsed" in:
			parse_videoUrl("https://b23.tv/av688730800") shouldEqual BiliVideoId(688730800L, "1T24y197V2")
			parse_videoUrl("http://b23.tv/BV1T24y197V2") shouldEqual BiliVideoId(688730800L, "1T24y197V2")
			parse_videoUrl("b23.tv/BV1T24y197V2") shouldEqual BiliVideoId(688730800L, "1T24y197V2")
		"b23 video link should not take www. or /video prefix" in:
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://www.b23.tv/av123456")
			an[IllegalArgumentException] should be thrownBy parse_videoUrl("https://b23.tv/video/av123456")
		
	}
	
	"while destruct b23.tv share link :" - {
		
		val examples = Table(
			("b23_link", "bilibili_video_link"),
			("https://b23.tv/iiCldvZ", "https://www.bilibili.com/video/BV1Gh411P7Sh?buvid=XY6F25B69BE9CF469FF5B917D012C93E95E72&is_story_h5=false&mid=wD6DQnYivIG5pfA3sAGL6A%3D%3D&p=1&plat_id=114&share_from=ugc&share_medium=android&share_plat=android&share_session_id=8081015b-1210-4dea-a665-6746b4850fcd&share_source=COPY&share_tag=s_i&timestamp=1689605644&unique_k=iiCldvZ&up_id=19977489"),
			("http://b23.tv/3ymowwx", "https://www.bilibili.com/video/BV15Y411n754?p=1&share_medium=android_i&share_plat=android&share_source=COPY&share_tag=s_i&timestamp=1650293889&unique_k=3ymowwx")
		)
		
		"not b23.tv link is not supported" in:
			an[IllegalArgumentException] should be thrownBy destructB23Url("sukazyo.cc/2xhUHO2e")
			an[IllegalArgumentException] should be thrownBy destructB23Url("https://sukazyo.cc/2xhUHO2e")
			an[IllegalArgumentException] should be thrownBy destructB23Url("长月烬明澹台烬心理分析向解析（一）因果之锁，渡魔之路")
			an[IllegalArgumentException] should be thrownBy destructB23Url("https://b23.tvb/JDo2eaD")
			an[IllegalArgumentException] should be thrownBy destructB23Url("https://ab23.tv/JDo2eaD")
		"b23.tv/avXXX video link is not supported" in:
			an[IllegalArgumentException] should be thrownBy destructB23Url("https://b23.tv/av123456")
			an[IllegalArgumentException] should be thrownBy destructB23Url("https://b23.tv/BV1Q541167Qg")
		
		forAll (examples) { (origin, result) =>
			s"b23 link $origin should be destructed to $result" taggedAs (Slow, Network) in:
				destructB23Url(origin) shouldEqual result
		}
		
	}
	
}