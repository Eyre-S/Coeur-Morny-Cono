package cc.sukazyo.cono.morny.test.extra.twitter

import cc.sukazyo.cono.morny.social_share.external.twitter.{parseTweetUrl, TweetUrlInformation}
import cc.sukazyo.cono.morny.test.MornyTests

class PackageTest extends MornyTests {
	
	"while parsing tweet url :" - {
		
		"normal twitter tweet share url should be parsed" in {
			parseTweetUrl("https://twitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldEqual(Some(TweetUrlInformation(
					"twitter.com", "ps_urine/status/1727614825755505032",
					"ps_urine", "1727614825755505032",
					None, Some("s=20")
				)))
		}
		
		"normal X.com tweet share url should be parsed" in {
			parseTweetUrl("https://x.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
		}
		
		"X.com or twitter tweet share url should not www.sensitive" in {
			parseTweetUrl("https://www.twitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("https://www.x.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
		}
		
		"fxtwitter and fixupx url should be parsed" in {
			parseTweetUrl("https://fxtwitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("https://fixupx.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
		}
		"vxtwitter should be parsed and can be with c." in {
			parseTweetUrl("https://vxtwitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("https://c.vxtwitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
		}
		"fixvx should be parsed and cannot be with c." in {
			parseTweetUrl("https://fixvx.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("https://c.fixvx.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(empty)
		}
		
		"fxtwitter and vxtwitter should not contains www." in {
			parseTweetUrl("https://www.fxtwitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(empty)
			parseTweetUrl("https://www.fixupx.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(empty)
			parseTweetUrl("https://www.vxtwitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(empty)
			parseTweetUrl("https://www.fixvx.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(empty)
		}
		
		"url should be http/s non-sensitive" in {
			parseTweetUrl("twitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("http://x.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("http://fxtwitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("http://fixupx.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("vxtwitter.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("fixvx.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
		}
		
		"url param should be non-sensitive" in {
			parseTweetUrl("twitter.com/ps_urine/status/1727614825755505032")
				.shouldBe(defined)
			parseTweetUrl("http://x.com/ps_urine/status/1727614825755505032/?q=ajisdl&form=ANNNB1&refig=5883b79c966b4881b79b50cb6f1c6c6a")
				.shouldBe(defined)
			parseTweetUrl("http://fxtwitter.com/ps_urine/status/1727614825755505032/?s=20")
				.shouldBe(defined)
			parseTweetUrl("http://fixupx.com/ps_urine/status/1727614825755505032?s=20")
				.shouldBe(defined)
			parseTweetUrl("vxtwitter.com/ps_urine/status/1727614825755505032")
				.shouldBe(defined)
			parseTweetUrl("fixvx.com/ps_urine/status/1727614825755505032?q=ajisdl&form=ANNNB1&refig=5883b79c966b4881b79b50cb6f1c6c6a")
				.shouldBe(defined)
		}
		
		"screen name should not be non-exists" in {
			parseTweetUrl("twitter.com/status/1727614825755505032")
				.shouldBe(empty)
			parseTweetUrl("http://x.com/status/1727614825755505032/?q=ajisdl&form=ANNNB1&refig=5883b79c966b4881b79b50cb6f1c6c6a")
				.shouldBe(empty)
			parseTweetUrl("http://fxtwitter.com/status/1727614825755505032/?s=20")
				.shouldBe(empty)
			parseTweetUrl("http://fixupx.com/status/1727614825755505032?s=20")
				.shouldBe(empty)
			parseTweetUrl("vxtwitter.com/status/1727614825755505032")
				.shouldBe(empty)
			parseTweetUrl("fixvx.com/status/1727614825755505032?q=ajisdl&form=ANNNB1&refig=5883b79c966b4881b79b50cb6f1c6c6a")
				.shouldBe(empty)
		}
		
		"url with photo id should be parsed" in {
			parseTweetUrl("twitter.com/ps_urine/status/1727614825755505032/photo/2")
				.should(matchPattern { case Some(TweetUrlInformation(_, _, _, _, Some("2"), _)) => })
			parseTweetUrl("http://x.com/ps_urine/status/1727614825755505032/photo/1/?q=ajisdl&form=ANNNB1&refig=5883b79c966b4881b79b50cb6f1c6c6a")
				.should(matchPattern { case Some(TweetUrlInformation(_, _, _, _, Some("1"), _)) => })
			parseTweetUrl("http://fxtwitter.com/ps_urine/status/1727614825755505032/photo/4/?s=20")
				.should(matchPattern { case Some(TweetUrlInformation(_, _, _, _, Some("4"), _)) => })
			parseTweetUrl("http://fixupx.com/ps_urine/status/1727614825755505032/photo/7?s=20")
				.should(matchPattern { case Some(TweetUrlInformation(_, _, _, _, Some("7"), _)) => })
			parseTweetUrl("vxtwitter.com/ps_urine/status/1727614825755505032/photo/114514")
				.should(matchPattern { case Some(TweetUrlInformation(_, _, _, _, Some("114514"), _)) => })
			parseTweetUrl("fixvx.com/ps_urine/status/1727614825755505032/photo/unavailable-id?q=ajisdl&form=ANNNB1&refig=5883b79c966b4881b79b50cb6f1c6c6a")
				.shouldBe(empty)
		}
		
	}
	
}
