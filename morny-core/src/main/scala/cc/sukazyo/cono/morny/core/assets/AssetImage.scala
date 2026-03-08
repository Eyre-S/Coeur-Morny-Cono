package cc.sukazyo.cono.morny.core.assets

import scala.util.Using

case class AssetImage (group: String, filepath: List[String]) {
	
	@throws[ReadAssetsException]("if read failed due to any reason.")
	def get (using assetsManager: MornyAssets): Array[Byte] = {
		try
			Using(assetsManager.get(group).read())
				{ _.readAllBytes() }
				.get
		catch case e: Throwable =>
			throw ReadAssetsException(group, filepath)
	}
	
}
