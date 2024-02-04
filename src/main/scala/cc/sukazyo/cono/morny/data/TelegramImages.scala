package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.core.MornyAssets
import cc.sukazyo.cono.morny.core.MornyAssets.AssetsException

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
	val IMG_404: AssetsFileImage = AssetsFileImage("images/http-sekai-404.png")
	val IMG_500: AssetsFileImage = AssetsFileImage("images/http-sekai-500.png")
	val IMG_501: AssetsFileImage = AssetsFileImage("images/http-sekai-501.png")
	val IMG_523: AssetsFileImage = AssetsFileImage("images/http-sekai-523.png")
	
}
