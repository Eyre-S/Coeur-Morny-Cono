package cc.sukazyo.cono.morny.core.assets

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.assets.MornyAssets.{AssetPackLoadException, NotAssetPackException}
import cc.sukazyo.restools.{ResourceDirectory, ResourcePackage}
import io.github.classgraph.ClassGraph

import java.net.URI
import scala.collection.mutable.ListBuffer

/** Utils for load assets packs.
  */
object AssetPackLoader {
	
	class ClasspathScanException (val classpath: URI, cause: Throwable)
		extends Exception(s"Error load resources of classpath $classpath", cause)
	
	private def scanClasspath (): (List[ResourceDirectory], List[ClasspathScanException]) = {
		val resPacks = ListBuffer.empty[ResourceDirectory]
		val errs = ListBuffer.empty[ClasspathScanException]
		ClassGraph().getClasspathURIs.forEach { path =>
			try {
				val pack = ResourcePackage.fromURI(path)
				Option(pack.getDirectory("assets")) match {
					case None =>
					case Some(dir) =>
						if dir.getFile(MornyAssets.METADATA_FILE) != null then
							resPacks += dir
				}
			} catch case e: Throwable =>
				errs += ClasspathScanException(path, e)
		}
		(resPacks.toList, errs.toList)
	}
	
	/** Scan all the classpaths, load available assets packs.
	  *
	  * An assets pack is loaded only when the classpath contains file
	  * `/assets/morny-assets.jsonc` (filename defined at [[MornyAssets.METADATA_FILE]]). The
	  * assets dir will be `/assets` dir.
	  */
	@throws[NotAssetPackException]
	@throws[AssetPackLoadException]
	def loadFromScans (assetsManager: MornyAssets): Unit = {
		val resDirs = scanClasspath()
		resDirs._2.foreach { err =>
			logger.warn(s"assets load failed for ${err.classpath} : ${err.getCause.getMessage}")
		}
		for (resDir <- resDirs._1) {
			assetsManager.register(resDir)
		}
	}
	
}
