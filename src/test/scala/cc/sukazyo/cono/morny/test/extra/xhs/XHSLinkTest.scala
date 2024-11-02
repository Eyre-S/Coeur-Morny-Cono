package cc.sukazyo.cono.morny.test.extra.xhs

import cc.sukazyo.cono.morny.extra.xhs.XHSLink.{matchShareUrl, matchUrl, searchShareText, searchShareUrl, searchUrls, ShareLink, ShareLinkTraditional, ShareLinkWithVariant}
import cc.sukazyo.cono.morny.extra.xhs.XHSLink
import cc.sukazyo.cono.morny.test.MornyTests

class XHSLinkTest extends MornyTests {
	
	"On constructing XHSLink" - {
		
		"by searching url from string, " - {
			
			case class TestUnit (text: String, parsed: ShareLink, parsed_url: String)
			type TestUnits = List[TestUnit]
			
			//noinspection HttpUrlsUsage
			val onlyShareUrls: TestUnits = List(
				TestUnit(
					"http://xhslink.com/h04FrT/?problem=false&fake=false&share=yes",
					ShareLinkTraditional("h04FrT"),
					"https://xhslink.com/h04FrT",
				),
				TestUnit(
					"http://xhslink.com/vzmV0Q",
					ShareLinkTraditional("vzmV0Q"),
					"https://xhslink.com/vzmV0Q",
				),
				TestUnit(
					"http://xhslink.com/a/Qb6N47BGvhBY",
					ShareLinkWithVariant("a", "Qb6N47BGvhBY"),
					"https://xhslink.com/a/Qb6N47BGvhBY",
				),
				TestUnit(
					"http://xhslink.com/B/G8bwBm",
					ShareLinkWithVariant("B", "G8bwBm"),
					"https://xhslink.com/B/G8bwBm",
				),
				TestUnit(
					"http://xhslink.com/C/Bd4jsz",
					ShareLinkWithVariant("C", "Bd4jsz"),
					"https://xhslink.com/C/Bd4jsz",
				)
			)
			//noinspection HttpUrlsUsage
			val withFormattedShareTexts: TestUnits = List(
				TestUnit(
					"47 Neko醤发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 ifOsA0C5cPQLfgu \uD83D\uDE06 http://xhslink.com/h04FrT，复制本条信息，打开【小红书】App查看精彩内容！",
					ShareLinkTraditional("h04FrT"),
					"https://xhslink.com/h04FrT",
				),
				TestUnit(
					"16 张背肌发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 Q6VRvJdtrHjQ34h \uD83D\uDE06 http://xhslink.com/Kr0PJG，复制本条信息，打开【小红书】App查看精彩内容！\n\n感觉可以投废物频道",
					ShareLinkTraditional("Kr0PJG"),
					"https://xhslink.com/Kr0PJG",
				),
				TestUnit(
					"\uD83D\uDE06 NH57DpH6oXqBVqA \uD83D\uDE06 http://xhslink.com/B/G8bwBm，复制本条信息，打开【小红书】App查看精彩内容！",
					ShareLinkWithVariant("B", "G8bwBm"),
					"https://xhslink.com/B/G8bwBm",
				),
				TestUnit(
					"22 温建兵律师发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 JweZ0vbwLLW5knv \uD83D\uDE06 http://xhslink.com/29rSGT，复制本条信息，打开【小红书】App查看精彩内容！",
					ShareLinkTraditional("29rSGT"),
					"https://xhslink.com/29rSGT",
				),
				TestUnit(
					"84 不太嚣张女士发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 BzXtgX7uQM79dPg \uD83D\uDE06 http://xhslink.com/C/Bd4jsz ，复制本条信息，打开【小红书】App查看精彩内容！",
					ShareLinkWithVariant("C", "Bd4jsz"),
					"https://xhslink.com/C/Bd4jsz",
				),
				TestUnit(
					"51 大盘鸡发布了一篇小红书笔记，快来看吧！ \uD83D\uDE06 Xa9x8sdx9io9axLaz \uD83D\uDE06 http://xhslink.com/a/Qb6N47BGvhBY ，复制本条信息，打开【小红书】App查看精彩内容！",
					ShareLinkWithVariant("a", "Qb6N47BGvhBY"),
					"https://xhslink.com/a/Qb6N47BGvhBY",
				)
			)
			//noinspection HttpUrlsUsage
			val withShareUrls: TestUnits = List(
				TestUnit(
					"大无语事件…DIY给爸妈办西班牙非盈利\n经历一番周折签证终于批下来了\n家人不在北京 护照拿回来路上才发现 签证给贴在了护照信息页…闻所未闻的操作 就离谱\n不出意外的话护照需要重新办理 source (https://xhslink.com/4vdlZS)",
					ShareLinkTraditional("4vdlZS"),
					"https://xhslink.com/4vdlZS",
				)
			) ::: withFormattedShareTexts ::: onlyShareUrls
			
			def foundByUrlMatch (testUnits: TestUnits): Unit = {
				
				"should individually be found by matchShareUrl" - {
					testUnits.zipWithIndex.foreach { (unit, index) => { s"in individual test #$index : ${unit.parsed_url}" in {
						val it = matchShareUrl(unit.text)
						//noinspection ScalaUnusedExpression
						it shouldEqual Some(unit.parsed)
						it.get.link shouldEqual unit.parsed_url
					}}}
				}
				
				"should individually be found by matchUrl" - {
					testUnits.zipWithIndex.foreach { (unit, index) => { s"in individual test $index : ${unit.parsed_url}" in {
						val it = matchUrl(unit.text)
						//noinspection ScalaUnusedExpression
						it shouldEqual Some(unit.parsed)
						it.get shouldBe a[ShareLink]
						it.get.asInstanceOf[ShareLink].link shouldEqual unit.parsed_url
					}}}
				}
				
			}
			
			def foundBYUrlSearch (testUnits: TestUnits): Unit = {
				
				"should individually be found by searchShareUrl" - {
					testUnits.zipWithIndex.foreach { (unit, index) => { s"in individual test $index : ${unit.parsed_url}" in {
						val it = searchShareUrl(unit.text)
						it should have size 1
						//noinspection ScalaUnusedExpression
						it.head shouldEqual unit.parsed
						it.head.link shouldEqual unit.parsed_url
					}}}
				}
				
				"should totally be found by searchShareUrl" in {
					val it = searchShareUrl(
						testUnits.map(_.text).mkString("\n")
					)
					//noinspection ScalaUnusedExpression
					it shouldEqual testUnits.map(_.parsed)
					(it zip testUnits.map(_.parsed_url))
						.foreach(it => it._1.link shouldEqual it._2)
				}
				
				"should individually be found by searchUrls" - {
					testUnits.zipWithIndex.foreach { (unit, index) => { s"in individual test $index : ${unit.parsed_url}" in {
						val it = searchUrls(unit.text)
						it should have size 1
						//noinspection ScalaUnusedExpression
						it.head shouldEqual unit.parsed
						it.head shouldBe a[ShareLink]
						it.head.asInstanceOf[ShareLink].link shouldEqual unit.parsed_url
					}}}
				}
				
				"should totally be found by searchUrls" in {
					val it = searchUrls(
						testUnits.map(_.text).mkString("\n")
					)
					//noinspection ScalaUnusedExpression
					it shouldEqual testUnits.map(_.parsed)
					(it zip testUnits.map(_.parsed_url)).foreach { (it, parsed) =>
						it shouldBe a[ShareLink]
						it.asInstanceOf[ShareLink].link shouldEqual parsed
					}
				}
				
			}
			
			def foundByShareTexts (testUnits: TestUnits): Unit = {
				
				"should individually be found by searchShareText" - {
					testUnits.zipWithIndex.foreach { (unit, index) => { s"in individual test $index : ${unit.parsed_url}" in {
						val it = searchShareText(unit.text)
						it should have size 1
						//noinspection ScalaUnusedExpression
						it.head shouldEqual unit.parsed
						it.head.link shouldEqual unit.parsed_url
					}}}
				}
				
				"should totally be found by searchShareText" in {
					val it = searchShareText(
						testUnits.map(_.text).mkString("\n")
					)
					//noinspection ScalaUnusedExpression
					it shouldEqual testUnits.map(_.parsed)
					(it zip testUnits.map(_.parsed_url)).foreach { (it, parsed) =>
						it shouldBe a[ShareLink]
						it.link shouldEqual parsed
					}
				}
				
			}
			
			"The plain share urls" - {
				foundByUrlMatch(onlyShareUrls)
				foundBYUrlSearch(withShareUrls)
			}
			
			"The texts with share url" - {
				foundBYUrlSearch(withShareUrls)
			}
			
			"The share texts" - {
				foundBYUrlSearch(withShareUrls)
				foundByShareTexts(withFormattedShareTexts)
			}
			
		}
		
	}
	
}
