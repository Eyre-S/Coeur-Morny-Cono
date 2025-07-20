package cc.sukazyo.cono.morny.core.assets

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.restools.{ResourceDirectory, ResourceFile, ResourcePackage}
import cc.sukazyo.restools.utils.PathsHelper
import io.github.classgraph.ClassGraph

import scala.collection.mutable.ListBuffer
import scala.util.boundary
import scala.util.boundary.break

class MornyAssets {
	
	private val assetPacks: ListBuffer[ResourceDirectory] = ListBuffer.empty
	
	ClassGraph().getClasspathURIs.forEach { path =>
		try {
			val pack = ResourcePackage.fromURI(path)
			Option(pack.getDirectory("assets")) match {
				case None =>
				case Some(dir) =>
					for (pack <- dir.listDirectories()) {
						assetPacks += pack
						logger.debug(s"loaded asset pack ${pack.getPath.last} from classpath $path")
					}
			}
		} catch case _: Throwable =>
			logger.warn("Failed to load assets pack from " + path)
	}
	
	def getFile (path: String*): ResourceFile = boundary[ResourceFile] {
		for (dir <- assetPacks) {
			val file = dir.getFile(path*)
			if file != null then break(file)
		}
		throw IllegalArgumentException(s"Asset file ${PathsHelper.compile(path.toArray)} cannot be found!")
	}
	
}

object MornyAssets {
	
	def inCoeur (using coeur: MornyCoeur): MornyAssets =
		coeur.assets
	
}
