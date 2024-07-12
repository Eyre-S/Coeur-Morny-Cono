package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.internal.BuildConfigField
import cc.sukazyo.cono.morny.system.utils.EpochDateTime.EpochMillis
import cc.sukazyo.cono.morny.util.FileUtils
import cc.sukazyo.cono.morny.BuildConfig
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString

import java.io.IOException
import java.net.URISyntaxException
import java.security.NoSuchAlgorithmException

object MornySystem {
	
	/** The normal version string without extra context (`+000` fields).
	  *
	  * Should contains base version ([[VERSION_BASE]]), with additional [[VERSION_DELTA version delta]]
	  * or snapshot tag if exists.
	  *
	  * For example `1.0.0`, `2.1.1-beta2`, `0.19.2-SNAPSHOT`, `2.0.0-alpha19-SNAPSHOT`
	  */
	@BuildConfigField val VERSION: String = BuildConfig.VERSION
	/** The full specific version string with extra context (`+000` fields).
	  *
	  * Should contains all the normal version ([[VERSION]]), additional with git commit hash
	  * if exists.
	  *
	  * For example `2.0.0-alpha19-SNAPSHOT+git254ec2a5`
	  */
	@BuildConfigField val VERSION_FULL: String = BuildConfig.VERSION_FULL
	/** The base version string.
	  *
	  * Contains only the main version number (0.0.0) and alpha/beta/RC version (-alpha20). NO
	  * snapshot tag or [[VERSION_DELTA version delta]] is included.
	  */
	@BuildConfigField val VERSION_BASE: String = BuildConfig.VERSION_BASE
	/** The version delta string if exists.
	  *
	  * This delta is for debug purpose when it is designed, it adds a field to the normal
	  * version string to help shows if the code is updated in debug environment.
	  *
	  * The standard version delta is a string starts with `-ð`, attached after the
	  * [[VERSION_BASE base version]].
	  *
	  * It is currently not in used in official environment yet.
	  */
	@BuildConfigField val VERSION_DELTA: Option[String] = BuildConfig.VERSION_DELTA
	/** The full length git commit id that this build is based on.
	  *
	  * Along the workspace is in a checked out git repo, this field will be the currently checked
	  * out git commit, no matter the git repo is clean or not.
	  *
	  * Only exists when this is built with git repo, if there's no git repo, this will only be
	  * [[None]].
	  */
	@BuildConfigField val GIT_COMMIT: Option[String] = Some(BuildConfig.COMMIT)
	/** The build time in [[EpochMillis]].
	  *
	  * If this build is built with a git repo and the git repo is clean, that means the source
	  * code should have no changes with [[GIT_COMMIT the currently checked out git commit]],
	  * then we call this is a **clean build**, and this timestamp will be the [[GIT_COMMIT]]'s
	  * commit timestamp. This can help us to do a *reproducible build*.
	  *
	  * On any other cases, this timestamp will be the [[System.currentTimeMillis]] when the
	  * `sbt compile` is called.
	  */
	@BuildConfigField val CODE_TIMESTAMP: EpochMillis = BuildConfig.CODE_TIMESTAMP
	/** The codename of this version.
	  *
	  * Usually a city name, and usually updates on and only on the minor version (X.Y.z) level
	  * update.
	  *
	  * Should be a lowercased english only string (maybe contains numbers at some point, at
	  * least not historically).
	  */
	@BuildConfigField val CODENAME: String = BuildConfig.CODENAME
	/** The HTTP url that a git repository of this project can be found.
	  */
	@BuildConfigField val CODE_STORE: String = BuildConfig.CODE_STORE
	/** The string template that the commit url of this build can be found.
	  *
	  * Should contains one `%s` placeholder, can be interpolated a [[GIT_COMMIT]] to get the
	  * page of commit that current build based on.
	  */
	//noinspection ScalaWeakerAccess
	@BuildConfigField val COMMIT_PATH: String = BuildConfig.COMMIT_PATH
	
	/** If this build is built with a clean git repo. */
	@BuildConfigField
	def isCleanBuild: Boolean = BuildConfig.CLEAN_BUILD
	
	/** The HTTP url of git commit which this build is based on.
	  * 
	  * Based on [[GIT_COMMIT]] and [[COMMIT_PATH]].
	  */
	def currentCodePath: String|Null =
		if ((COMMIT_PATH eq null) || (GIT_COMMIT isEmpty)) null
		else COMMIT_PATH.formatted(GIT_COMMIT get)
	
	/** The MD5 hash of current running coeur execution jar.
	  * 
	  * Only works on the current running coeur is from a jar file.
	  * 
	  * If read jar file failed, it possibly means currently is not run from a jar file, then
	  * this will return `"<non-jar-runtime>"`.
	  * 
	  * If failed on calculating MD5 hash, it will return `"<calculation-error>"`.
	  */
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
