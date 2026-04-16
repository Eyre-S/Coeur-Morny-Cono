package cc.sukazyo.cono.morny.core.assets

import cc.sukazyo.cono.morny.MornyCoreFixtureTests
import cc.sukazyo.cono.morny.data.TelegramImages
import org.scalatest.Outcome
import org.scalatest.freespec.FixtureAnyFreeSpec

class TestAssetImages extends MornyCoreFixtureTests {
	
	type FixtureParam = MornyAssets
	override def withFixture (test: OneArgTest): Outcome = {
		val assets = MornyAssets()
		AssetPackLoader.loadFromScans(assets)
		test(assets)
	}
	
	"Images" - {
		
		"defined with AssetImage" - {
			
			"(that exists)" - {
				"should be able to get" in { assets =>
					noException should be thrownBy {
						AssetImage("core-tests", "scala-icon.png" :: Nil).get(using assets)
					}
				}
			}
			
		}
		
		"defined with AssetImageSpec" - {
			
			"should be able to get" in { assets =>
				noException should be thrownBy {
					TelegramImages.IMG_ABOUT.get(using assets)
				}
			}
			
		}
		
	}
	
}
