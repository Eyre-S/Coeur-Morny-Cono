package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.data.MornyAssets

import java.io.IOException

object MornyAbout {
	
	lazy val MORNY_PREVIEW_IMAGE_ASCII: String =
		try { MornyAssets.coreAssets.getFile("texts/server-hello.txt").readString }
		catch case e: IOException =>
			throw RuntimeException("Cannot read MORNY_PREVIEW_IMAGE_ASCII from assets pack", e)
	
	lazy val MORNY_SPLASHES: Array[String] =
		try { MornyAssets.coreAssets.getFile("/texts/splash.txt").readString.split('\n') }
		catch case e: IOException =>
			throw RuntimeException("Cannot read MORNY_SPLASHES from assets pack", e)
	
	val MORNY_SOURCECODE_LINK = "https://github.com/Eyre-S/Coeur-Morny-Cono"
	val MORNY_SOURCECODE_SELF_HOSTED_MIRROR_LINK = "https://storage.sukazyo.cc/Eyre_S/Coeur-Morny-Cono"
	val MORNY_ISSUE_TRACKER_LINK = "https://github.com/Eyre-S/Coeur-Morny-Cono/issues"
	val MORNY_USER_GUIDE_LINK = "https://book.sukazyo.cc/morny"
	
}
