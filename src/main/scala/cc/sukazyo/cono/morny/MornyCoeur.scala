package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.bot.command.MornyCommands
import cc.sukazyo.cono.morny.daemon.MornyDaemons
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.MornyCoeur.THREAD_SERVER_EXIT
import cc.sukazyo.cono.morny.bot.api.EventListenerManager
import cc.sukazyo.cono.morny.bot.event.{MornyEventListeners, MornyOnInlineQuery, MornyOnTelegramCommand, MornyOnUpdateTimestampOffsetLock}
import cc.sukazyo.cono.morny.bot.query.MornyQueries
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.GetMe

import scala.util.boundary
import scala.util.boundary.break

object MornyCoeur {
	
	val THREAD_SERVER_EXIT = "system-exit"
	
}

class MornyCoeur (using val config: MornyConfig) {
	
	given MornyCoeur = this
	
	///>>> BLOCK START instance configure & startup stage 1
	
	logger info "Coeur starting..."
	
	logger info s"args key:\n  ${config.telegramBotKey}"
	if config.telegramBotUsername ne null then
		logger info s"login as:\n  ${config.telegramBotUsername}"
	
	private val __loginResult = login()
	if (__loginResult eq null)
		logger error "Login to bot failed."
		System exit -1
	
	configure_exitCleanup()
	
	///<<< BLOCK END instance configure & startup stage 1
	
	/** [[TelegramBot]] account of this Morny */
	val account: TelegramBot = __loginResult.account
	/** [[account]]'s telegram username */
	val username: String = __loginResult.username
	/** [[account]]'s telegram user id */
	val userid: Long = __loginResult.userid
	
	/** current Morny's [[MornyTrusted]] instance */
	val trusted: MornyTrusted = MornyTrusted()
	
	val daemons: MornyDaemons = MornyDaemons()
	//noinspection ScalaWeakerAccess
	val eventManager: EventListenerManager = EventListenerManager()
	eventManager register MornyOnUpdateTimestampOffsetLock()
	val commands: MornyCommands = MornyCommands()
	//noinspection ScalaWeakerAccess
	val queries: MornyQueries = MornyQueries()
	eventManager register MornyOnTelegramCommand(using commands)
	eventManager register MornyOnInlineQuery(using queries)
	//noinspection ScalaUnusedSymbol
	val events: MornyEventListeners = MornyEventListeners(using eventManager)
	
	/** inner value: about why morny exit, used in [[daemon.MornyReport]]. */
	private var whileExit_reason: AnyRef|Null = _
	def exitReason: AnyRef|Null = whileExit_reason
	val coeurStartTimestamp: Long = ServerMain.systemStartupTime
	
	///>>> BLOCK START instance configure & startup stage 2
	
	daemons.start()
	logger info "start telegram event listening"
	account setUpdatesListener eventManager
	if config.commandLoginRefresh then
		logger info "resetting telegram command list"
		commands.automaticTGListUpdate()
	
	daemons.reporter.reportCoeurMornyLogin()
	logger info "Coeur start complete."
	
	///<<< BLOCK END instance configure & startup stage 2
	
	def saveDataAll(): Unit = {
		// nothing to do
		logger info "done all save action."
	}
	
	private def exitCleanup (): Unit = {
		daemons.reporter.reportCoeurExit()
		account.shutdown()
		logger info "stopped bot account"
		daemons.stop()
		if config.commandLogoutClear then
			commands.automaticTGListRemove()
		logger info "done exit cleanup"
	}
	
	private def configure_exitCleanup (): Unit = {
		Runtime.getRuntime.addShutdownHook(new Thread(() => exitCleanup(), THREAD_SERVER_EXIT))
	}
	
	def exit (status: Int, reason: AnyRef): Unit =
		whileExit_reason = reason
		System exit status
	
	private case class LoginResult(account: TelegramBot, username: String, userid: Long)
	
	private def login (): LoginResult|Null = {
		
		val builder = TelegramBot.Builder(config.telegramBotKey)
		var api_bot = config.telegramBotApiServer
		var api_file = config.telegramBotApiServer4File
		if (api_bot ne null)
			if api_bot endsWith "/" then api_bot = api_bot dropRight 1
			if !(api_bot endsWith "/bot") then api_bot += "/bot"
			builder.apiUrl(api_bot)
		if (api_file ne null)
			if api_file endsWith "/file/" then api_file = api_file dropRight 1
			if !(api_file endsWith "/file/bot") then api_file += "/file/bot"
			builder.apiUrl(api_bot)
		if ((api_bot ne null) || (api_file ne null))
			logger info
					s"""Telegram bot api set to:
					   |- bot: $api_bot
					   |- file: $api_file"""
					.stripMargin
		
		val account = builder build
		
		logger info "Trying to login..."
		boundary[LoginResult|Null] {
			for (i <- 0 to 3) {
				if i > 0 then logger info "retrying..."
				try {
					val remote = (account execute GetMe()).user
					if ((config.telegramBotUsername ne null) && config.telegramBotUsername != remote.username)
						throw RuntimeException(s"Required the bot @${config.telegramBotUsername} but @${remote.username} logged in")
					logger info s"Succeed logged in to @${remote.username}"
					break(LoginResult(account, remote.username, remote.id))
				} catch
					case r: boundary.Break[LoginResult|Null] => throw r
					case e =>
						logger error
							s"""${exceptionLog(e)}
							   |login failed"""
								.stripMargin
			}
			null
		}
		
	}
 
}
