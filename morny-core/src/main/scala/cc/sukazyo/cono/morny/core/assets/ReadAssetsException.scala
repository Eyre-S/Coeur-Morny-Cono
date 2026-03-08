package cc.sukazyo.cono.morny.core.assets

import cc.sukazyo.restools.utils.PathsHelper

class ReadAssetsException private (message: String) extends Exception(message)

object ReadAssetsException {
	
	def apply (namespace: String, path: Seq[String]): ReadAssetsException =
		new ReadAssetsException(s"Failed to read asset file: $namespace:${PathsHelper.compile(path.toArray)}")
	
	def apply (message: String): ReadAssetsException =
		new ReadAssetsException(message)
	
}
