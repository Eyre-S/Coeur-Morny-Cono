package cc.sukazyo.cono.morny.core.assets

import cc.sukazyo.cono.morny.MornyCoreTests
import org.scalatest.Inspectors

class TestAssetsPackLoads extends MornyCoreTests with Inspectors {
	
	"Assets packs" - {
		
		"loads by classpaths scan" - {
			
			"should loads 'morny.core' assets pack" in {
				val assets = MornyAssets()
				AssetPackLoader.loadFromScans(assets)
				forExactly (1, assets.assetPacks) { pack =>
					pack.metadata.id shouldEqual "morny.core"
				}
			}
			
			"should loads 'morny.core.test' assets pack" in {
				val assets = MornyAssets()
				AssetPackLoader.loadFromScans(assets)
				forExactly(1, assets.assetPacks) { pack =>
					pack.metadata.id shouldEqual "morny.core.test"
				}
			}
			
		}
		
	}
	
}
