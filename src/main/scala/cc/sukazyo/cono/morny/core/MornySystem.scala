package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.internal.BuildConfigField
import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis
import cc.sukazyo.cono.morny.util.FileUtils
import cc.sukazyo.cono.morny.BuildConfig
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString

import java.io.IOException
import java.net.URISyntaxException
import java.security.NoSuchAlgorithmException

object MornySystem {
	
	@BuildConfigField val VERSION: String = BuildConfig.VERSION
	@BuildConfigField val VERSION_FULL: String = BuildConfig.VERSION_FULL
	@BuildConfigField val VERSION_BASE: String = BuildConfig.VERSION_BASE
	@BuildConfigField val VERSION_DELTA: Option[String] = BuildConfig.VERSION_DELTA
	@BuildConfigField val GIT_COMMIT: Option[String] = Some(BuildConfig.COMMIT)
	@BuildConfigField val CODE_TIMESTAMP: EpochMillis = BuildConfig.CODE_TIMESTAMP
	@BuildConfigField val CODENAME: String = BuildConfig.CODENAME
	@BuildConfigField val CODE_STORE: String = BuildConfig.CODE_STORE
	//noinspection ScalaWeakerAccess
	@BuildConfigField val COMMIT_PATH: String = BuildConfig.COMMIT_PATH
	
	@BuildConfigField
	def isCleanBuild: Boolean = BuildConfig.CLEAN_BUILD
	
	def currentCodePath: String|Null =
		if ((COMMIT_PATH eq null) || (GIT_COMMIT isEmpty)) null
		else COMMIT_PATH.formatted(GIT_COMMIT get)
	
	def getJarMD5: String = {
		try {
			FileUtils.getMD5Three(MornySystem.getClass.getProtectionDomain.getCodeSource.getLocation.toURI.getPath)
		} catch
			//noinspection ScalaUnnecessaryParentheses
			case _: (IOException|URISyntaxException) =>
				"<non-jar-runtime>"
			case n: NoSuchAlgorithmException =>
				logger `error` n.toLogString
//				MornyReport.exception(n, "<coeur-md5/calculation-error>") // todo: will not implemented
				"<calculation-error>"
	}
	
}
