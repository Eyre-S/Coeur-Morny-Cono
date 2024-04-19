package cc.sukazyo.cono.morny.data

import cc.sukazyo.restools.ResourcesPackage

object MornyAssets {
	
	class AssetsException (caused: Throwable) extends Exception("Cannot read assets file.", caused)
	
	val pack: ResourcesPackage = ResourcesPackage(MornyAssets.getClass, "assets_morny")
	
}
