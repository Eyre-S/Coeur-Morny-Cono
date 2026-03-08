package cc.sukazyo.cono.morny.core.assets

trait AssetsImageSpec {
	
	protected def assetsNamespace: String
	
	protected def image (namespace: String, filename: String, ext: String): AssetImage = {
		AssetImage(namespace, "images" :: s"$filename.$ext" :: Nil)
	}
	
	protected def image (filename: String, ext: String): AssetImage =
		image (this.assetsNamespace, filename, ext)
	
	protected def png (filename: String): AssetImage =
		this.image(filename, "png")
	
}
