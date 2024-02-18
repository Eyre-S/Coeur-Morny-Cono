package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyConfig.CheckFailure
import cc.sukazyo.cono.morny.util.CommonFormat

import java.time.ZoneOffset
import java.util.TimeZone
import scala.collection.mutable.ArrayBuffer

object ServerMain {
	
	val tz: TimeZone = TimeZone getDefault
	val tz_offset: ZoneOffset = ZoneOffset `ofTotalSeconds` (tz.getRawOffset / 1000)
	
	private val THREAD_MORNY_INIT: String = "morny-init"
	
	def main (args: Array[String]): Unit = {
		
		val config = new MornyConfig.Prototype()
		var mode_echoVersion = false
		var mode_echoHello = false
		var mode_testRun = false
		var showHello = true
		
		val unknownArgs = ArrayBuffer[String]()
		val deprecatedArgs = ArrayBuffer[(String, String)]()
		
		var i = 0
		while (i < args.length) {
			args(i) match {
				
				case "-d" | "--debug" =>
					Log.debug(true)
					config.debugMode = true
				case "--debug-run" =>
					config.debugMode = true
				case "--dbg" =>
					Log.debug(true)
					deprecatedArgs += "--dbg" -> "--verbose-logging"
				case "--verbose-logging" | "--verbose" =>
					Log.debug(true)
				case "-t" | "--test" => mode_testRun = true
				
				case "--no-hello" | "-hf" | "--quiet" | "-q" => showHello = false
				case "--only-hello" | "-ho" | "-o" | "-hi" => mode_echoHello = true
				case "--version" | "-v" => mode_echoVersion = true
				
				// deprecated: use --outdated-ignore instead
				case "--outdated-block" | "-ob" =>
					config.eventIgnoreOutdated = true
					deprecatedArgs += "--outdated-block" -> "--outdated-ignore"
				case "--outdated-ignore" | "-oig" => config.eventIgnoreOutdated = true
				
				case "--api" | "-a" => i += 1; config.telegramBotApiServer = args(i)
				case "--api-files" | "files-api" | "-af" => i += 1; config.telegramBotApiServer4File = args(i)
				
				case "--token" | "-t" => i += 1; config.telegramBotKey = args(i)
				case "--username" | "-u" => i += 1; config.telegramBotUsername = args(i)
				
				case "--master" | "-mm" => i += 1; config.trustedMaster = args(i) toLong
				case "--trusted-chat" | "-trs" => i += 1; config.trustedChat = args(i) toLong
				case "--report-to" => i += 1; config.reportToChat = args(i) toLong
				case "--report-zone" => i += 1; config.reportZone = TimeZone.getTimeZone(args(i))
				
				case "--trusted-reader-dinner" | "-trsd" => i += 1; config.dinnerTrustedReaders `add` (args(i) toLong)
				case "--dinner-chat" | "-chd" => i += 1; config.dinnerChatId = args(i) toLong
				
				case "--http-listen-port" | "-hp" =>
					i += 1
					config.httpPort = args(i) toInt
				
				case "--medication-notify-chat" | "-medc" => i += 1; config.medicationNotifyToChat = args(i) toLong
				case "--medication-notify-timezone" | "-medtz" =>
					i += 1
					config.medicationTimerUseTimezone = ZoneOffset.ofHours(args(i) toInt)
				case "--medication-notify-times" | "-medt" =>
					i += 1
					for (u <- args(i) `split` ",") {
						config.medicationNotifyAt `add` (u toInt)
					}
				
				case "--auto-cmd-list" | "-ca" => config.commandLoginRefresh = true
				case "--auto-cmd-remove" | "-cr" => config.commandLogoutClear = true
				case "--auto-cmd" | "-cmd" | "-c" =>
					config.commandLoginRefresh = true
					config.commandLogoutClear = true
				
				case _ => unknownArgs append args(i)
				
			}
			i += 1
		}
		
		/// Setup launch params from ENVIRONMENT
		var propToken: String = null
		var propTokenKey: String = null
		for (iKey <- MornyConfig.PROP_TOKEN_KEY) {
			if ((System `getenv` iKey) != null) {
				propToken = System `getenv` iKey
				propTokenKey = iKey
			}
		}
		
		///
		/// Output startup message
		/// process startup params - like startup mode
		///
		
		if (showHello) logger `info` MornyAbout.MORNY_PREVIEW_IMAGE_ASCII
		if (mode_echoHello) return
		
		if (unknownArgs.nonEmpty) logger `warn`
			s"""Can't understand arg to some meaning
			   |  ${unknownArgs mkString "\n  "}"""
				.stripMargin
		if (deprecatedArgs.nonEmpty) logger `warn`
			s"""Those arguments have been deprecated:
			   |  ${deprecatedArgs map((d, n) => s"$d : use $n instead") mkString "\n  "}
			   |""".stripMargin
		
		if (config.debugMode && Log.debug)
			logger `warn`
				"""Coeur Debug mode enabled.
				  |
				  |  The debug log will be outputted, and caches will be disabled.
				  |  It will cause much unnecessary performance cost, may caused extremely slow down on your bot.
				  |  Make sure that you are not in production environment.
				  |  
				  |  Since 2.0.0, this mode is the combined of the two following options:
				  |    --debug-run       enable coeur debug mode, that will disabled all the caches.
				  |    --verbose-logging enable the logger to output debug/trace logs."""
					.stripMargin
		else if (config.debugMode)
			logger `warn`
				"""Coeur Debug mode enabled.
				  |
				  |  All the bot caches will be disabled.
				  |  It will cause much unnecessary performance cost, may caused extremely slow down on your bot.
				  |  Make sure that you are not in production environment."""
					.stripMargin
		else if (Log debug)
			logger `warn`
				"""Debug log output enabled.
				  |  It will output much more debug/trace logs, may lower your performance,
				  |  so make sure that you are not in production environment."""
					.stripMargin
		
		if (mode_echoVersion) {
			
			logger `info`
				s"""Morny Cono Version
				   |- version :
				   |    Morny ${MornySystem.CODENAME toUpperCase}
				   |    ${MornySystem.VERSION_BASE}${
					MornySystem.VERSION_DELTA match {
						case Some(d) => "-δ" + d
						case None => ""
					}
				}
				   |- md5hash :
				   |    ${MornySystem.getJarMD5}
				   |- gitstat :
				   |${
					MornySystem.GIT_COMMIT match {
						case Some(commit) =>
							s"""    on commit ${if (MornySystem.isCleanBuild) "- clean-build" else "<δ/non-clean-build>"}
							   |    $commit"""
								.stripMargin
						case None => "    <non-git-build>"
					}
				}
				   |- buildtd :
				   |    ${MornySystem.CODE_TIMESTAMP}
				   |    ${CommonFormat.formatDate(MornySystem.CODE_TIMESTAMP, 0)} [UTC]"""
					.stripMargin
			return
			
		}
		
		logger `info`
			s"""ServerMain.java Loaded >>>
			   |- version ${MornySystem.VERSION_FULL}
			   |- Morny ${MornySystem.CODENAME toUpperCase}
			   |- <${MornySystem.getJarMD5}> [${MornySystem.CODE_TIMESTAMP}]""".stripMargin
		
		// due to [[MornyFormatterConsole]] will use a localized time, it will output to the log
		logger `info` s"logging time will use time-zone ${tz.getID} ($tz_offset)"
		
		///
		/// Check Coeur arguments
		/// finally start Coeur Program
		///
		
		if (propToken != null) {
			config.telegramBotKey = propToken
			logger `info` s"Parameter <token> set by EnvVar $$$propTokenKey"
		}
		
		Thread.currentThread `setName` THREAD_MORNY_INIT
		
		try
			MornyCoeur(
				ServerModulesLoader.load()
			)(using config build)(
				testRun = mode_testRun
			)
		catch {
			case _: CheckFailure.NullTelegramBotKey =>
				logger.info("Parameter required has no value:\n --token.")
			case e: CheckFailure =>
				logger.error("Unknown failure occurred while starting ServerMain!:")
				e.printStackTrace(System.out)
		}
	}
	
}
