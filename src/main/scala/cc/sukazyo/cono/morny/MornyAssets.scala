package cc.sukazyo.cono.morny

import cc.sukazyo.restools.{ResourceDirectory, ResourcePackage}

object MornyAssets {
	
	class AssetsException (caused: Throwable) extends Exception("Cannot read assets file.", caused)
	
	val pack: ResourceDirectory = ResourcePackage.get("assets_morny").getDirectory("assets_morny")
	
}
