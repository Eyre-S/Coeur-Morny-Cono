package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.bot.command.MornyCommands
import cc.sukazyo.cono.morny.daemon.MornyDaemons
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.MornyCoeur.THREAD_MORNY_EXIT
import cc.sukazyo.cono.morny.bot.api.TelegramUpdatesListener
import cc.sukazyo.cono.morny.bot.event.MornyEventListeners
import cc.sukazyo.cono.morny.util.tgapi.ExtraAction
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.GetMe

import scala.util.boundary
import scala.util.boundary.break

object MornyCoeur {
	
	val THREAD_MORNY_EXIT = "morny-exiting"
	
	private var INSTANCE: MornyCoeur|Null = _
	
	val coeurStartTimestamp: Long = ServerMain.systemStartupTime
	
	def account: TelegramBot = INSTANCE.account
	def username: String = INSTANCE.username
	def userid: Long = INSTANCE.userid
	def config: MornyConfig = INSTANCE.my_config
	def trusted: MornyTrusted = INSTANCE.trusted
	def extra: ExtraAction = INSTANCE.extra
	
	def available: Boolean = INSTANCE != null
	def callSaveData(): Unit = INSTANCE.saveDataAll()
	
	def exitReason: AnyRef|Null = INSTANCE.whileExit_reason
	
	def exit (status: Int, reason: AnyRef): Unit =
		INSTANCE.whileExit_reason = reason
		System exit status
	
	def init (using config: MornyConfig): Unit = {
		
		if (INSTANCE ne null)
			logger error "Coeur already started!!!"
			return;
		
		logger info "Coeur starting..."
		INSTANCE = MornyCoeur()
		
		MornyDaemons.start()
		
		logger info "start telegram event listening"
		MornyEventListeners.registerAllEvents()
		INSTANCE.account.setUpdatesListener(TelegramUpdatesListener)
		
		if config.commandLoginRefresh then
			logger info "resetting telegram command list"
			MornyCommands.automaticTGListUpdate()
		
		logger info "Coeur start complete."
		
	}
	
}

class MornyCoeur (using config: MornyConfig) {
	def my_config: MornyConfig = config
	
	logger info s"args key:\n  ${config.telegramBotKey}"
	if config.telegramBotUsername ne null then
		logger info s"login as:\n  ${config.telegramBotUsername}"
	
	private val __loginResult = login()
	if (__loginResult eq null)
		logger error "Login to bot failed."
		System exit -1
	
	configure_exitCleanup()
	
	val account: TelegramBot = __loginResult.account
	val username: String = __loginResult.username
	val userid: Long = __loginResult.userid
	
	val trusted: MornyTrusted = MornyTrusted(using this)
	val extra: ExtraAction = ExtraAction as __loginResult.account
	var whileExit_reason: AnyRef|Null = _
	
	def saveDataAll(): Unit = {
		// nothing to do
		logger info "done all save action."
	}
	
	private def exitCleanup (): Unit = {
		MornyDaemons.stop()
		if config.commandLogoutClear then
			MornyCommands.automaticTGListRemove()
	}
	
	private def configure_exitCleanup (): Unit = {
		Runtime.getRuntime.addShutdownHook(new Thread(() => exitCleanup(), THREAD_MORNY_EXIT))
	}
	
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
