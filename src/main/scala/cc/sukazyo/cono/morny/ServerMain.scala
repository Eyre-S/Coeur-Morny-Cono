package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.Log.logger
import cc.sukazyo.cono.morny.MornyConfig.CheckFailure
import cc.sukazyo.cono.morny.util.CommonFormat

import java.time.ZoneOffset
import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps

object ServerMain {
	
	private val THREAD_MORNY_INIT: String = "morny-init"
	
	def main (args: Array[String]): Unit = {
		
		val config = new MornyConfig.Prototype()
		var mode_echoVersion = false
		var mode_echoHello = false
		var showHello = true
		
		val unknownArgs = ArrayBuffer[String]()
		val deprecatedArgs = ArrayBuffer[(String, String)]()
		
		var i = 0
		while (i < args.length) {
			args(i) match {
				
				case "-d" | "--dbg" | "--debug" => Log.debug(true)
				
				case "--no-hello" | "-hf" | "--quiet" | "-q" => showHello = false
				case "--only-hello" | "-ho" | "-o" | "-hi" => mode_echoHello = true
				case "--version" | "-v" => mode_echoVersion = true
				
				// deprecated: use --outdated-ignore instead
				case "--outdated-block" | "-ob" =>
					config.eventIgnoreOutdated = true
					deprecatedArgs += "--outdated-block" -> "--outdated-ignore"
				case "--outdated-ignore" | "-oig" => config.eventIgnoreOutdated = true
				
				case "--api" | "-a" => i+=1 ; config.telegramBotApiServer = args(i)
				case "--api-files" | "files-api" | "-af" => i+=1; config.telegramBotApiServer4File = args(i)
				
				case "--token" | "-t" => i+=1 ; config.telegramBotKey = args(i)
				case "--username" | "-u" => i+=1 ; config.telegramBotUsername = args(i)
				
				case "--master" | "-mm" => i+=1 ; config.trustedMaster = args(i)toLong
				case "--trusted-chat" | "-trs" => i+=1 ; config.trustedChat = args(i)toLong
				case "--report-to" => i+=1; config.reportToChat = args(i)toLong
				
				case "--trusted-reader-dinner" | "-trsd" => i+=1 ; config.dinnerTrustedReaders add (args(i)toLong)
				case "--dinner-chat" | "-chd" => i+=1 ; config.dinnerChatId = args(i)toLong
				
				case "--medication-notify-chat" | "-medc" => i+=1 ; config.medicationNotifyToChat = args(i)toLong
				case "--medication-notify-timezone" | "-medtz" =>
					i+=1
					config.medicationTimerUseTimezone = ZoneOffset.ofHours(args(i)toInt)
				case "--medication-notify-times" | "-medt" =>
					i+=1
					for (u <- args(i) split ",") {
						config.medicationNotifyAt add (u toInt)
					}
				
				case "--auto-cmd-list" | "-ca" => config.commandLoginRefresh = true
				case "--auto-cmd-remove" | "-cr" => config.commandLogoutClear = true
				case "--auto-cmd" | "-cmd" | "-c" =>
					config.commandLoginRefresh = true
					config.commandLogoutClear = true
				
				case _ => unknownArgs append args(i)
				
			}
			i+=1
		}
		
		/// Setup launch params from ENVIRONMENT
		var propToken: String = null
		var propTokenKey: String = null
		for (iKey <- MornyConfig.PROP_TOKEN_KEY) {
			if ((System getenv iKey) != null) {
				propToken = System getenv iKey
				propTokenKey = iKey
			}
		}
		
		///
		/// Output startup message
		/// process startup params - like startup mode
		///
		
		if (showHello) logger info MornyAbout.MORNY_PREVIEW_IMAGE_ASCII
		if (mode_echoHello) return;
		
		if (unknownArgs.nonEmpty) logger warn
				s"""Can't understand arg to some meaning
				   |  ${unknownArgs mkString "\n  "}"""
				.stripMargin
		if (deprecatedArgs.nonEmpty) logger warn
			s"""Those arguments have been deprecated:
			   |  ${deprecatedArgs map ((d, n) => s"$d : use $n instead") mkString "\n  "}
			   |""".stripMargin
		
		if (Log debug)
			logger warn
					"""Debug log output enabled.
					  |  It may lower your performance, make sure that you are not in production environment."""
					.stripMargin
		
		if (mode_echoVersion) {
			
			logger info
					s"""Morny Cono Version
					   |- version :
					   |    Morny ${MornySystem.CODENAME toUpperCase}
					   |    ${MornySystem.VERSION_BASE}${if (MornySystem.isUseDelta) "-δ"+MornySystem.VERSION_DELTA else ""}
					   |- md5hash :
					   |    ${MornySystem.getJarMD5}
					   |- gitstat :
					   |${ if (MornySystem.isGitBuild) {
							s"""    on commit ${if (MornySystem.isCleanBuild) "- clean-build" else "<δ/non-clean-build>"}
							   |    ${BuildConfig.COMMIT}"""
							.stripMargin
						} else "    <non-git-build>"}
					   |- buildtd :
					   |    ${BuildConfig.CODE_TIMESTAMP}
					   |    ${CommonFormat.formatDate(BuildConfig.CODE_TIMESTAMP, 0)} [UTC]"""
				.stripMargin
			return
			
		}
		
		logger info
				s"""ServerMain.java Loaded >>>
				   |- version ${MornySystem.VERSION_FULL}
				   |- Morny ${MornySystem.CODENAME toUpperCase}
				   |- <${MornySystem.getJarMD5}> [${BuildConfig.CODE_TIMESTAMP}]""".stripMargin
		
		///
		/// Check Coeur arguments
		/// finally start Coeur Program
		///
		
		if (propToken != null) {
			config.telegramBotKey = propToken
			logger info s"Parameter <token> set by EnvVar $$$propTokenKey"
		}
		
		Thread.currentThread setName THREAD_MORNY_INIT
		
		try
			MornyCoeur(using config build)
		catch {
			case _: CheckFailure.NullTelegramBotKey =>
				logger.info("Parameter required has no value:\n --token.")
			case e: CheckFailure =>
				logger.error("Unknown failure occurred while starting ServerMain!:")
				e.printStackTrace(System.out)
		}
	}
	
}
