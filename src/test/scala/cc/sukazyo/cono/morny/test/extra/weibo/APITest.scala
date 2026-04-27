package cc.sukazyo.cono.morny.test.extra.weibo

import cc.sukazyo.cono.morny.extra.weibo.{MApi, MStatus, parseWeiboStatusUrl}
import cc.sukazyo.cono.morny.test.MornyTests
import cc.sukazyo.cono.morny.test_tags.API
import org.scalatest.tagobjects.{Network, Slow}

import java.io.ByteArrayInputStream
import javax.imageio.ImageIO

class APITest extends MornyTests {
	
	"on using Weibo MAPI, " - {
		
		val validURL: String = "https://weibo.com/7985105960/Q1aWqfuNC"
		
		"to fetch weibo status, " - {
			
			"use stateuses_show " - {
				
				"with a valid status ID should return a MStatus API result" taggedAs(Slow, Network, API) in {
					val result = MApi.Fetch.statuses_show(parseWeiboStatusUrl(validURL).get.id)
					result.ok shouldBe 1
					result.data shouldBe a[MStatus]
				}
				
			}
			
			"the fetched MStatus" - {
				
				lazy val fetchedStatus: MStatus = MApi.Fetch.statuses_show(parseWeiboStatusUrl(validURL).get.id).data
				
				"should contains pics" taggedAs (Network, Slow, API) in {
					fetchedStatus.pics shouldBe defined
					fetchedStatus.pics.get should not be empty
				}
				
				"the pics" - {
					
					"should be able to fetched from its URL" taggedAs(Slow, Network, API) in {
						val pic = fetchedStatus.pics.get(0)
						val imageBytes = MApi.Fetch.pic(pic.url)
						val image = ImageIO.read(ByteArrayInputStream(imageBytes))
						image should not be null
						image.getWidth should be > 0
						image.getHeight should be > 0
					}
					
				}
				
			}
			
		}
		
	}
	
}
