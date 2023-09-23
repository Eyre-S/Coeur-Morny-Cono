package cc.sukazyo.cono.morny.data

import cc.sukazyo.cono.morny.{BuildConfig, MornyAbout, MornySystem}

import java.net.InetAddress
import java.rmi.UnknownHostException

object MornyInformation {
	
	//noinspection ScalaWeakerAccess
	def getVersionGitTagHTML: String = {
		if (!MornySystem.isGitBuild) return ""
		val g = StringBuilder()
		val cm = BuildConfig.COMMIT substring(0, 8)
		val cp = MornySystem.currentCodePath
		if (cp == null) g ++= s"<code>$cm</code>"
		else g ++= s"<a href='$cp'>$cm</a>"
		if (!MornySystem.isCleanBuild) g ++= ".<code>δ</code>"
		g toString
	}
	
	def getVersionAllFullTagHTML: String = {
		val v = StringBuilder()
		v ++= s"<code>${MornySystem VERSION_BASE}</code>"
		if (MornySystem isUseDelta) v ++= s"-δ<code>${MornySystem VERSION_DELTA}</code>"
		if (MornySystem isGitBuild) v ++= "+git." ++= getVersionGitTagHTML
		v ++= s"*<code>${MornySystem.CODENAME toUpperCase}</code>"
		v toString
	}
	
	//noinspection ScalaWeakerAccess
	def getRuntimeHostname: String | Null = {
		try InetAddress.getLocalHost.getHostName
		catch case _: UnknownHostException => null
	}
	
	def getAboutPic: Array[Byte] = TelegramImages.IMG_ABOUT get
	
	def getMornyAboutLinksHTML: String =
		s"""<a href='${MornyAbout MORNY_SOURCECODE_LINK}'>source code</a> | <a href='${MornyAbout MORNY_SOURCECODE_SELF_HOSTED_MIRROR_LINK}'>backup</a>
		   |<a href='${MornyAbout MORNY_ISSUE_TRACKER_LINK}'>反馈 / issue tracker</a>
		   |<a href='${MornyAbout MORNY_USER_GUIDE_LINK}'>使用说明书 / user guide & docs</a>"""
			.stripMargin
	
}
