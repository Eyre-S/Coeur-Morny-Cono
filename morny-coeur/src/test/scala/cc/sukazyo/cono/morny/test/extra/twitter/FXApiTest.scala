package cc.sukazyo.cono.morny.test.extra.twitter

import cc.sukazyo.cono.morny.social_share.external.twitter.FXApi
import cc.sukazyo.cono.morny.social_share.external.twitter.FXApi.Fetch
import cc.sukazyo.cono.morny.test.MornyTests
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.tagobjects.{Network, Slow}

//noinspection ScalaUnusedExpression
class FXApiTest extends MornyTests with TableDrivenPropertyChecks {
	
	"while fetch status (tweet) :"  - {
		
		"non exists tweet id should return 404" taggedAs (Slow, Network) in {
			val api = Fetch.status(Some("some_non_exists"), "-1")
			api.code shouldEqual 404
			api.message shouldEqual "NOT_FOUND"
			api.tweet shouldBe empty
		}
		
		/** It should return 401, but in practice it seems will only
		  * return 404.
		  */
		"private tweet should return 410 or 404" taggedAs (Slow, Network) in {
			val api = Fetch.status(Some("_takiChan"), "1586671758999924736")
			api.code should (equal (404) or equal (401))
			api.code match
				case 401 =>
					api.message shouldEqual "PRIVATE_TWEET"
					note("from private tweet got 401 PRIVATE_TWEET")
				case 404 =>
					api.message shouldEqual "NOT_FOUND"
					note("from private tweet got 404 NOT_FOUND")
			api.tweet shouldBe empty
		}
		
		val examples = Table[(Option[String], String), FXApi =>Unit](
			("id", "checking"),
			// Due to those tweets owner (Eyre_S) is now private, so the test will fail.
//			((Some("_Eyre_S"), "1669362743332438019"), api => {
//				api.code shouldEqual 200
//				api.tweet shouldBe defined
//				api.tweet.get.text shouldEqual "猫头猫头鹰头猫头鹰头猫头鹰"
//				api.tweet.get.quote shouldBe defined
//				api.tweet.get.quote.get.id shouldEqual "1669302279386828800"
//			}),
//			((None, "1669362743332438019"), api => {
//				api.code shouldEqual 200
//				api.tweet shouldBe defined
//				api.tweet.get.text shouldEqual "猫头猫头鹰头猫头鹰头猫头鹰"
//				api.tweet.get.quote shouldBe defined
//				api.tweet.get.quote.get.id shouldEqual "1669302279386828800"
//			}),
//			((None, "1654080016802807809"), api => {
//				api.code shouldEqual 200
//				api.tweet shouldBe defined
//				api.tweet.get.media shouldBe defined
//				api.tweet.get.media.get.videos shouldBe empty
//				api.tweet.get.media.get.photos shouldBe defined
//				api.tweet.get.media.get.photos.get.length shouldBe 1
//				api.tweet.get.media.get.photos.get.head.width shouldBe 2048
//				api.tweet.get.media.get.photos.get.head.height shouldBe 1536
//				api.tweet.get.media.get.mosaic shouldBe empty
//			}),
			// https://x.com/_suk_ws/status/1472085698081484800
			((Some("_suk_ws"), "1472085698081484800"), api => {
				api.code shouldEqual 200
				api.tweet shouldBe defined
				api.tweet.get.text shouldEqual "今年的工房要做年报&^&"
				// this tweet is single
				api.tweet.get.quote shouldBe empty
			}),
			// https://x.com/_suk_ws/status/1463410234504802306
			((None, "1463410234504802306"), api => {
				api.code shouldEqual 200
				api.tweet shouldBe defined
				api.tweet.get.text shouldEqual "bread-card-ui 平面拟物&~&"
				// this tweet is single
				api.tweet.get.quote shouldBe empty
				// this tweet has 1 photo and no video, only one media shouldn't be mosaic
				api.tweet.get.media shouldBe defined
				api.tweet.get.media.get.videos shouldBe empty
				api.tweet.get.media.get.photos shouldBe defined
				api.tweet.get.media.get.photos.get.length shouldBe 1
				api.tweet.get.media.get.mosaic shouldBe empty
			}),
			// https://x.com/_suk_ws/status/1463099580149424131
			((Some("_suk_ws"), "1463099580149424131"), api => {
				api.code shouldEqual 200
				api.tweet shouldBe defined
				// this tweet has 3 photos and no video. multiple photos will be mosaic
				api.tweet.get.media shouldBe defined
				api.tweet.get.media.get.videos shouldBe empty
				api.tweet.get.media.get.photos shouldBe defined
				api.tweet.get.media.get.photos.get.length shouldBe 3
				api.tweet.get.media.get.mosaic shouldBe defined
				// this tweet has a quote
				api.tweet.get.quote shouldBe defined
				api.tweet.get.quote.get.id shouldEqual "1463084178023452674"
			}),
			((None, "1538536152093044736"), api => {
				api.code shouldEqual 200
				api.tweet shouldBe defined
				api.tweet.get.media shouldBe defined
				api.tweet.get.media.get.videos shouldBe empty
				api.tweet.get.media.get.photos shouldBe defined
				api.tweet.get.media.get.photos.get.length shouldBe 2
				api.tweet.get.media.get.photos.get.head.width shouldBe 2894
				api.tweet.get.media.get.photos.get.head.height shouldBe 4093
				api.tweet.get.media.get.photos.get(1).width shouldBe 2894
				api.tweet.get.media.get.photos.get(1).height shouldBe 4093
				api.tweet.get.media.get.mosaic shouldBe defined
			})
		)
		forAll(examples) { (data, assertion) =>
			s"tweet $data should be fetched successful" taggedAs (Slow, Network) in {
				assertion(Fetch.status(data._1, data._2))
			}
		}
		
	}
	
}
