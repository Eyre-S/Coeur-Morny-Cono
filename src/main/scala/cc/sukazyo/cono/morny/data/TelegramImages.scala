package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.MornyAssets
import cc.sukazyo.cono.morny.daemon.MornyReport
import cc.sukazyo.cono.morny.MornyAssets.AssetsException

import java.io.IOException
import scala.language.postfixOps
import scala.util.Using

object TelegramImages {
	
	class AssetsFileImage (assetsPath: String) {
		
		private var cache: Array[Byte]|Null = _
		
		@throws[AssetsException]
		def get:Array[Byte] =
			if cache eq null then read()
			cache
		
		@throws[AssetsException]
		private def read (): Unit = {
			Using ((MornyAssets.pack getResource assetsPath)read) { stream =>
				try { this.cache = stream.readAllBytes() }
				catch case e: IOException => {
					throw AssetsException(e)
				}
			}
		}
		
	}
	
	val IMG_ABOUT: AssetsFileImage = AssetsFileImage("images/featured-image@0.5x.jpg")
	
}
