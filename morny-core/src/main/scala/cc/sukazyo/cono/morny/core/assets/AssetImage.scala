package cc.sukazyo.cono.morny.core.assets

import scala.util.Using

/** An image in an assets group.
  *
  * Image is located at <code>&lt;assets-root&gt;/_[[group]]_/images/_[[filepath]]_</code>. The [[filepath]] must
  * contains file extension.
  *
  * Usually is defined by [[AssetsImageSpec]].
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
