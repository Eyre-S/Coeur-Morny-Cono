package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.data.MornyAssets.AssetsException

import java.io.IOException
import scala.language.postfixOps
import scala.util.Using

object TelegramImages {
	
	class AssetsFileImage (assetsPath: List[String]) {
		
		private var cache: Option[Array[Byte]] = None
		
		@throws[AssetsException]
		def get:Array[Byte] =
			if cache isEmpty then read()
			cache.get
		
		@throws[AssetsException]
		private def read (): Unit = {
			Using (MornyAssets.assets.getFile(assetsPath*).read()) { stream =>
				try { this.cache = Some(stream.readAllBytes()) }
				catch case e: IOException => {
					throw AssetsException(e)
				}
			}
		}
		
	}
	
	object AssetsFileImage {
		def byId (id: String): AssetsFileImage =
			byId(id, "png")
		def byId (id: String, ty: String): AssetsFileImage =
			AssetsFileImage("images" :: s"$id.$ty" :: Nil);
	}
	
	val IMG_ABOUT: AssetsFileImage = AssetsFileImage.byId("featured-image@0.5x", "jpg")
	val IMG_400: AssetsFileImage = AssetsFileImage.byId("http-sekai-400")
	val IMG_404: AssetsFileImage = AssetsFileImage.byId("http-sekai-404")
	val IMG_500: AssetsFileImage = AssetsFileImage.byId("http-sekai-500")
	val IMG_501: AssetsFileImage = AssetsFileImage.byId("http-sekai-501")
	val IMG_523: AssetsFileImage = AssetsFileImage.byId("http-sekai-523")
	
}
