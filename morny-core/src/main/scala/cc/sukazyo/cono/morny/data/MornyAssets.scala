package cc.sukazyo.cono.morny.data

import cc.sukazyo.restools.{ResourceDirectory, ResourcePackage}

object MornyAssets {
	
	class AssetsException (caused: Throwable) extends Exception("Cannot read assets file.", caused)
	
	val assetsLocation: List[String] = "assets" :: "morny-coeur" :: Nil
	
	val pack: ResourcePackage = ResourcePackage.get(assetsLocation :+ "morny-coeur.identifier" *)
	val corePack: ResourcePackage = ResourcePackage.get("assets/core/core_assets.identifier")
	val assets: ResourceDirectory = pack.getDirectory(assetsLocation*)
	val coreAssets: ResourceDirectory = corePack.getDirectory("assets/core")
	
}
