package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.core.MornyCoeur.*
import cc.sukazyo.cono.morny.core.bot.api.{EventListenerManager, MornyCommandManager, MornyQueryManager}
import cc.sukazyo.cono.morny.core.bot.api.messages.ThreadingManager
import cc.sukazyo.cono.morny.core.bot.event.{MornyOnInlineQuery, MornyOnTelegramCommand, MornyOnUpdateTimestampOffsetLock}
import cc.sukazyo.cono.morny.core.bot.internal.{ErrorMessageManager, ThreadingManagerImpl}
import cc.sukazyo.cono.morny.core.http.api.{HttpServer, MornyHttpServerContext}
import cc.sukazyo.cono.morny.core.http.internal.MornyHttpServerContextImpl
import cc.sukazyo.cono.morny.reporter.MornyReport
import cc.sukazyo.cono.morny.util.schedule.Scheduler
import cc.sukazyo.cono.morny.util.EpochDateTime.EpochMillis
import cc.sukazyo.cono.morny.util.time.WatchDog
import cc.sukazyo.cono.morny.util.GivenContext
import cc.sukazyo.cono.morny.util.UseString.MString
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.GetMe

import scala.util.boundary
import scala.util.boundary.break

object MornyCoeur {
	
	val THREAD_SERVER_EXIT = "system-exit"
	
	/** A tag that shows current [[MornyCoeur!]] is running under
	  * test mode.
	  *
	  * @see *Test Mode* in [[MornyCoeur]] for more introduction for test mode.
	  * @since 2.0.0
	  */
	object TestRun
	
	case class OnInitializingPreContext (
		externalContext: GivenContext,
		coeurStartupTimes: EpochMillis,
		account: TelegramBot,
		username: String,
		userid: Long,
		tasks: Scheduler,
		trusted: MornyTrusted,
		eventManager: EventListenerManager,
		commandManager: MornyCommandManager,
		queryManager: MornyQueryManager,
		httpServer: MornyHttpServerContext,
		givenCxt: GivenContext
	)
	
	case class OnInitializingContext (
		externalContext: GivenContext,
		coeurStartupTimes: EpochMillis,
		account: TelegramBot,
		username: String,
		userid: Long,
		tasks: Scheduler,
		trusted: MornyTrusted,
		eventManager: EventListenerManager,
		commandManager: MornyCommandManager,
		queryManager: MornyQueryManager,
		httpServer: MornyHttpServerContext,
		givenCxt: GivenContext
	)
	
	case class OnInitializingPostContext (
		externalContext: GivenContext,
		coeurStartupTimes: EpochMillis,
		account: TelegramBot,
		username: String,
		userid: Long,
		tasks: Scheduler,
		trusted: MornyTrusted,
		eventManager: EventListenerManager,
		commandManager: MornyCommandManager,
		queryManager: MornyQueryManager,
		httpServer: MornyHttpServerContext,
		givenCxt: GivenContext
	)
	
	case class OnStartingContext (
		givenCxt: GivenContext
	)
	
	case class OnStartingPostContext (
		givenCxt: GivenContext
	)
	
}

/** The Coeur (Main Class) of Morny.
  *
  * ## Lifecycle
  *
  * todo
  *
  * ## Test Mode
  *
  * Coeur supports a special launch mode called test mode.When running
  * under test mode, this instance will only run till the initialize
  * is complete and exit immediately. In the lifecycle, it is run till
  * [[MornyModule.onInitializingPost]] event.
  *
  * In this way, it will be available to see if the initialize result is
  * under expected while not really start the service (aka. will not
  * take side effect).
  *
  * This mode can be enabled by the parameter [[testRun]]. When exited
  * under this mode, the [[exitReason]] will be a special value [[TestRun]].
  *
  * @param modules all morny modules.
  * @param config the immutable [[MornyConfig]] instance, will use all
  *               over this Morny (Coeur) instance.
  * @param testRun if the instance should run under test mode.
  */
class MornyCoeur (modules: List[MornyModule])(using val config: MornyConfig)(testRun: Boolean = false) {
	
	given MornyCoeur = this
	
	val externalContext: GivenContext = GivenContext()
	import cc.sukazyo.cono.morny.util.dataview.Table.format as fmtTable
	logger `info`
		m"""The following Modules have been added to current Morny:
		   |${fmtTable(
			"Module ID" :: "Module Name" :: "Module Version" :: Nil,
			modules.map(f => f.id :: f.name :: f.version :: Nil)*
		)}
		   |"""
	
	///>>> BLOCK START instance configure & startup stage 1
	
	logger `info` "Coeur starting..."
	private var initializeContext = GivenContext()
	
	import cc.sukazyo.cono.morny.util.StringEnsure.deSensitive
	logger `info` s"args key:\n  ${config.telegramBotKey.deSensitive(4)}"
	if config.telegramBotUsername ne null then
		logger `info` s"login as:\n  ${config.telegramBotUsername}"
	
	private val __loginResult: LoginResult = login() match
		case some: Some[LoginResult] => some.get
		case None =>
			logger `error` "Login to bot failed."
			System `exit` -1
			throw RuntimeException()
	initializeContext << __loginResult
	
	///<<< BLOCK END instance configure & startup stage 1
	
	/** inner value: about why morny exit, used in [[daemon.MornyReport]]. */
	private var whileExit_reason: Option[AnyRef] = None
	/** About why morny exits. */
	def exitReason: Option[AnyRef] = whileExit_reason
	/** Stores when current Morny Coeur instance starts to initialize.
	  * 
	  * The time is older than login but earlier than Coeur's daemons initialize.
	  * 
	  * in milliseconds.
	  */
	val coeurStartTimestamp: EpochMillis = System.currentTimeMillis
	
	/** [[TelegramBot]] account of this Morny */
	val account: TelegramBot = __loginResult.account
	/** [[account]]'s telegram username */
	val username: String = __loginResult.username
	/** [[account]]'s telegram user id */
	val userid: Long = __loginResult.userid
	
	/** Morny's task [[Scheduler]] */
	val tasks: Scheduler = Scheduler()
	/** current Morny's [[MornyTrusted]] instance */
	val trusted: MornyTrusted = MornyTrusted()
	private val _messageThreading: ThreadingManagerImpl = ThreadingManagerImpl(using account)
	val messageThreading: ThreadingManager = _messageThreading
	val errorMessageManager: ErrorMessageManager = ErrorMessageManager()
	
	val eventManager: EventListenerManager = EventListenerManager()
	val commands: MornyCommandManager = MornyCommandManager()
	val queries: MornyQueryManager = MornyQueryManager()
	
	private var _httpServerContext: MornyHttpServerContext = MornyHttpServerContextImpl()
	
	// Coeur Initializing Pre Event
	modules.foreach(it => it.onInitializingPre(OnInitializingPreContext(
		externalContext,
		coeurStartTimestamp, account, username, userid, tasks, trusted,
		eventManager, commands, queries,
		_httpServerContext,
		initializeContext)))
	
	// register core/api events
	eventManager register MornyOnUpdateTimestampOffsetLock()
	eventManager register MornyOnTelegramCommand(using commands)
	eventManager register MornyOnInlineQuery(using queries)
	eventManager register _messageThreading.NextMessageCatcher
	{ // register core commands
		import bot.command.*
		val $MornyHellos = MornyHellos()
		val $MornyInformation = MornyInformation()
		val $MornyInformationOlds = MornyInformationOlds(using $MornyInformation)
		val $MornyManagers = MornyManagers()
		commands.register(
			
			$MornyHellos.On,
			$MornyHellos.Hello,
			MornyInfoOnStart(),
			
			$MornyManagers.SaveData,
			$MornyInformation,
			$MornyInformationOlds.Version,
			$MornyInformationOlds.Runtime,
			$MornyManagers.Exit,
			
			DirectMsgClear(),
			_messageThreading.CancelCommand,
			errorMessageManager.ShowErrorMessageCommand,
			
		)
	}
	// register core http api service
	import cc.sukazyo.cono.morny.core.http.services as http_srv
	_httpServerContext register4API http_srv.Ping()
	
	// Coeur Initializing Event
	modules.foreach(it => it.onInitializing(OnInitializingContext(
		externalContext,
		coeurStartTimestamp, account, username, userid, tasks, trusted,
		eventManager, commands, queries,
		_httpServerContext,
		initializeContext)))
	
	val watchDog: WatchDog = WatchDog("watch-dog", 1000, 1500, { (consumed, _) =>
		import cc.sukazyo.cono.morny.util.CommonFormat.formatDuration as f
		logger `warn`
			s"""Can't keep up! is the server overloaded or host machine fall asleep?
			   |  current tick takes ${f(consumed)} to complete.""".stripMargin
		tasks.notifyIt()
	})
	initializeContext / this << watchDog
	
	// Coeur Initializing Post Event
	modules.foreach(it => it.onInitializingPost(OnInitializingPostContext(
		externalContext,
		coeurStartTimestamp, account, username, userid, tasks, trusted,
		eventManager, commands, queries,
		_httpServerContext,
		initializeContext)))
	
	///>>> BLOCK START instance configure & startup stage 2
	
	logger `info` "done initialize."
	if testRun then
		logger `info` "done test run, exiting."
		this.exit(0, TestRun)
	
	// Coeur Starting Pre
	configure_exitCleanup()
	
	// Coeur Starting Event
	modules.foreach(it => it.onStarting(OnStartingContext(
		initializeContext)))
	
	logger `info` "start http server"
	val http: HttpServer = _httpServerContext.start
	_httpServerContext = null
	logger `info` "start telegram event listening"
	import com.pengrad.telegrambot.TelegramException
	account.setUpdatesListener(eventManager, (e: TelegramException) => {
		
		// This function intended to catch exceptions on update
		//   fetching controlled by Telegram API Client. So that
		//   it won't be directly printed to STDOUT without Morny's
		//   logger. And it can be reported when needed.
		// TelegramException can either contains a caused that infers
		//   a lower level client exception (network err or others);
		//   nor contains a response that means API request failed.
		
		if (e.response != null) {
			import com.google.gson.GsonBuilder
			logger `error`
				s"""Failed get updates: ${e.getMessage}
				   |  server responses:
				   |${GsonBuilder().setPrettyPrinting().create.toJson(e.response).indent(4)}
				   |""".stripMargin
			externalContext.consume[MornyReport](_.exception(e, "Failed get updates."))
		}
		
		if (e.getCause != null) {
			import java.net.{SocketException, SocketTimeoutException}
			import javax.net.ssl.SSLHandshakeException
			val caused = e.getCause
			caused match
				case e_timeout: (SSLHandshakeException|SocketException|SocketTimeoutException) =>
					import cc.sukazyo.messiva.log.Message

					import scala.collection.mutable
					val log = mutable.ArrayBuffer(s"Failed get updates: Network Error")
					var current: Throwable = e_timeout
					log += s"  due to: ${current.getClass.getSimpleName}: ${current.getMessage}"
					while (current.getCause != null) {
						current = current.getCause
						log += s"  caused by: ${current.getClass.getSimpleName}: ${current.getMessage}"
					}
					logger `error` Message(log mkString "\n")
				case e_other =>
					logger `error`
						s"""Failed get updates:
						   |${e_other.toLogString `indent` 3}""".stripMargin
					externalContext.consume[MornyReport](_.exception(e_other, "Failed get updates."))
		}
		
	})
	
	// Coeur Starting Post Event
	modules.foreach(it => it.onStartingPost(OnStartingPostContext(
		initializeContext)))
	
	if config.commandLoginRefresh then
		logger `info` "resetting telegram command list"
		commands.automaticTGListUpdate()
	
	initializeContext = null
	logger `info` "Coeur start complete."
	
	///<<< BLOCK END instance configure & startup stage 2
	
	def saveDataAll(): Unit = {
		modules.foreach(it => it.onRoutineSavingData)
		logger `notice` "done all save action."
	}
	
	private def exitCleanup (): Unit = {
		
		// Morny Exiting
		modules.foreach(it => it.onExiting)
		
		account.removeGetUpdatesListener()
		logger `info` "stopped bot update listener"
		tasks.waitForStop()
		logger `info` s"morny tasks stopped: remains ${tasks.amount} tasks not be executed"
		
		// Morny Exiting Post
		if config.commandLogoutClear then
			commands.automaticTGListRemove()
		modules.foreach(it => it.onExitingPost)
		
		account.shutdown()
		logger `info` "stopped bot account"
		// Morny Exited
		modules.foreach(it => it.onExited)
		logger `info` "done exit cleanup\nMorny will EXIT now"
		
	}
	
	private def configure_exitCleanup (): Unit = {
		Runtime.getRuntime.addShutdownHook(new Thread(() => exitCleanup(), THREAD_SERVER_EXIT))
	}
	
	def exit (status: Int, reason: AnyRef): Unit =
		whileExit_reason = Some(reason)
		System `exit` status
	
	private case class LoginResult(account: TelegramBot, username: String, userid: Long)
	
	private def login (): Option[LoginResult] = {
		
		val builder = TelegramBot.Builder(config.telegramBotKey)
		var api_bot = config.telegramBotApiServer
		var api_file = config.telegramBotApiServer4File
		if (api_bot ne null)
			if api_bot `endsWith` "/" then api_bot = api_bot dropRight 1
			if !(api_bot `endsWith` "/bot") then api_bot += "/bot"
			builder.apiUrl(api_bot)
		if (api_file ne null)
			if api_file `endsWith` "/file/" then api_file = api_file dropRight 1
			if !(api_file `endsWith` "/file/bot") then api_file += "/file/bot"
			builder.apiUrl(api_bot)
		if ((api_bot ne null) || (api_file ne null))
			logger `info`
					s"""Telegram bot api set to:
					   |- bot: $api_bot
					   |- file: $api_file"""
					.stripMargin
		
		val account = builder build
		
		logger `info` "Trying to login..."
		boundary[Option[LoginResult]] {
			for (i <- 0 to 3) {
				if i > 0 then logger `info` "retrying..."
				try {
					import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.execute
					val remote = GetMe().execute(using account).user
					if ((config.telegramBotUsername ne null) && config.telegramBotUsername != remote.username)
						throw RuntimeException(s"Required the bot @${config.telegramBotUsername} but @${remote.username} logged in")
					logger `info` s"Succeed logged in to @${remote.username}"
					break(Some(LoginResult(account, remote.username, remote.id)))
				} catch
					case r: boundary.Break[Option[LoginResult]] => throw r
					case e =>
						logger `error`
							s"""${e.toLogString}
							   |login failed"""
								.stripMargin
			}
			None
		}
		
	}
	
}
