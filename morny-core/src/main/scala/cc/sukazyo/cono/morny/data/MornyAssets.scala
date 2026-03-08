package cc.sukazyo.cono.morny.data

import cc.sukazyo.restools.{ResourceDirectory, ResourcePackage}

@deprecated("use modern API: cc.sukazyo.cono.morny.core.assets.MornyAssets", "2.0.0-alpha22")
object MornyAssets {
	
	class AssetsException (caused: Throwable) extends Exception("Cannot read assets file.", caused)
	
	val assetsLocation: List[String] = "assets" :: "morny-coeur" :: Nil
	
	val pack: ResourcePackage = ResourcePackage.get(assetsLocation :+ "morny-coeur.identifier" *)
	val corePack: ResourcePackage = ResourcePackage.get("assets/core/startup-assets")
	val assets: ResourceDirectory = pack.getDirectory(assetsLocation*)
	val coreAssets: ResourceDirectory = corePack.getDirectory("assets/core")
	
}
