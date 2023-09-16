package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.internal.BuildConfigField
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.daemon.MornyReport
import cc.sukazyo.cono.morny.util.FileUtils

import java.io.IOException
import java.net.URISyntaxException
import java.security.NoSuchAlgorithmException

object MornySystem {
	
	@BuildConfigField val VERSION: String = BuildConfig.VERSION
	@BuildConfigField val VERSION_FULL: String = BuildConfig.VERSION_FULL
	@BuildConfigField val VERSION_BASE: String = BuildConfig.VERSION_BASE
	@BuildConfigField val VERSION_DELTA: String = BuildConfig.VERSION_DELTA
	@BuildConfigField val CODENAME: String = BuildConfig.CODENAME
	@BuildConfigField val CODE_STORE: String = BuildConfig.CODE_STORE
	@BuildConfigField val COMMIT_PATH: String = BuildConfig.COMMIT_PATH
	
	@BuildConfigField
	def isUseDelta: Boolean = VERSION_DELTA ne null
	
	@BuildConfigField
	def isGitBuild: Boolean = BuildConfig.COMMIT ne null
	
	@BuildConfigField
	def isCleanBuild: Boolean = BuildConfig.CLEAN_BUILD
	
	def currentCodePath: String|Null =
		if ((COMMIT_PATH eq null) || (!isGitBuild)) null
		else COMMIT_PATH.formatted(BuildConfig.COMMIT)
	
	def getJarMD5: String = {
		try {
			FileUtils.getMD5Three(MornySystem.getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath)
		} catch
			//noinspection ScalaUnnecessaryParentheses
			case _: (IOException|URISyntaxException) =>
				"<non-jar-runtime>"
			case n: NoSuchAlgorithmException =>
				logger error exceptionLog(n)
				MornyReport.exception(n, "<coeur-md5/calculation-error>")
				"<calculation-error>"
	}
	
}
