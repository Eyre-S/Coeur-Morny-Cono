package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.MornyAssets

import scala.language.postfixOps
import scala.util.Using
import java.io.IOException
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.daemon.MornyReport

object TelegramImages {
	
	class AssetsFileImage (assetsPath: String) {
		
		private var cache: Array[Byte]|Null = _
		
		def get:Array[Byte] =
			if cache eq null then read()
			if cache eq null then throw IllegalStateException("Failed to get assets file image.")
			cache
		
		private def read (): Unit = {
			Using ((MornyAssets.pack getResource assetsPath)read) { stream =>
				try { this.cache = stream.readAllBytes() }
				catch case e: IOException => {
					logger error
							s"""Cannot read resource file:
							   |${exceptionLog(e)}""".stripMargin
					MornyReport.exception(e, "Cannot read resource file.")
				}
			}
		}
		
	}
	
	val IMG_ABOUT: AssetsFileImage = AssetsFileImage("images/featured-image@0.5x.jpg")
	
}
