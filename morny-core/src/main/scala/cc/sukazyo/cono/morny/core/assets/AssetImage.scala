package cc.sukazyo.cono.morny.core.assets

import scala.util.Using

/** An image.
  */
case class AssetImage (group: String, filepath: List[String]) {
	
	private lazy val realPath: List[String] = "images" :: filepath
	
	@throws[ReadAssetsException]("if read failed due to any reason.")
	def get (using assetsManager: MornyAssets): Array[Byte] = {
		try
			Using(assetsManager.get(group, realPath*).read())
				{ _.readAllBytes() }
				.get
		catch case e: Throwable =>
			throw ReadAssetsException(group, realPath).initCause(e)
	}
	
}
