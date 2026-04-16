package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.core.assets.{AssetImage, AssetsImageSpec}

import scala.language.postfixOps

object TelegramImages extends AssetsImageSpec {
	
	override protected def assetsNamespace: String = "core"
	
	val IMG_ABOUT: AssetImage = image("featured-image@0.5x", "jpg")
	val IMG_400: AssetImage = png("http-sekai-400")
	val IMG_404: AssetImage = png("http-sekai-404")
	val IMG_500: AssetImage = png("http-sekai-500")
	val IMG_501: AssetImage = png("http-sekai-501")
	val IMG_523: AssetImage = png("http-sekai-523")
	
}
