package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.MornyAssets
import cc.sukazyo.cono.morny.MornyAssets.AssetsException

import java.io.IOException
import scala.language.postfixOps
import scala.util.Using

object TelegramImages {
	
	class AssetsFileImage (assetsPath: String) {
		
		private var cache: Option[Array[Byte]] = None
		
		@throws[AssetsException]
		def get:Array[Byte] =
			if cache isEmpty then read()
			cache.get
		
		@throws[AssetsException]
		private def read (): Unit = {
			Using ((MornyAssets.pack getResource assetsPath)read) { stream =>
				try { this.cache = Some(stream.readAllBytes()) }
				catch case e: IOException => {
					throw AssetsException(e)
				}
			}
		}
		
	}
	
	val IMG_ABOUT: AssetsFileImage = AssetsFileImage("images/featured-image@0.5x.jpg")
	
}
