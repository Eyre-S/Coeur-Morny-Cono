package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.bot.api.OnUpdate;
import cc.sukazyo.cono.morny.bot.command.MornyCommands;
import cc.sukazyo.cono.morny.bot.event.EventListeners;
import cc.sukazyo.cono.morny.bot.query.MornyQueries;
import cc.sukazyo.cono.morny.daemon.MornyDaemons;
import cc.sukazyo.cono.morny.daemon.TrackerDataManager;
import cc.sukazyo.cono.morny.util.tgapi.ExtraAction;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.impl.FileApi;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.GetMe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cc.sukazyo.cono.morny.Log.logger;

/**
 * Morny Cono 核心<br>
 * - 的程序化入口类，保管着 morny 的核心属性<br>
 */
public class MornyCoeur {
	
	/** 当前程序的 Morny Coeur 实例 */
	private static MornyCoeur INSTANCE;
	
	/** 当前 Morny 的启动配置 */
	public final MornyConfig config;
	
	/** 当前 Morny 的{@link MornyTrusted 信任验证机}实例 */
	private final MornyTrusted trusted;
	/** 当前 Morny 的 telegram 命令管理器 */
	private final MornyCommands commandManager = new MornyCommands();
	private final MornyQueries queryManager = new MornyQueries();
	
	/** morny 的 bot 账户 */
	private final TelegramBot account;
	private final ExtraAction extraActionInstance;
	/**
	 * morny 的 bot 账户的用户名<br>
	 * <br>
	 * 这个字段将会在登陆成功后赋值为登录到的 bot 的 username。
	 * 它应该是和 {@link #account} 的 username 同步的<br>
	 * <br>
	 * 如果在登陆之前就定义了此字段，则登陆代码会验证登陆的 bot 的 username
	 * 是否与定义的 username 符合。如果不符合则会报错。
	 */
	public final String username;
	/**
	 * morny 的 bot 账户的 telegram id<br>
	 * <br>
	 * 这个字段将会在登陆成功后赋值为登录到的 bot 的 id。
	 */
	public final long userid;
	/**
	 * morny 主程序启动时间<br>
	 * 用于统计数据
	 */
	public static final long coeurStartTimestamp = ServerMain.systemStartupTime;
	
	private Object whileExitReason = null;
	
	private record LogInResult(TelegramBot account, String username, long userid) { }
	
	/**
	 * 执行 bot 初始化
	 *
	 * @param config Morny 实例的配置选项数据
	 */
	private MornyCoeur (MornyConfig config) {
		
		this.config = config;
		
		configureSafeExit();
		
		logger.info("args key:\n  " + config.telegramBotKey);
		if (config.telegramBotUsername != null) {
			logger.info("login as:\n  " + config.telegramBotUsername);
		}
		
		try {
			final LogInResult loginResult = login(config.telegramBotApiServer, config.telegramBotApiServer4File, config.telegramBotKey, config.telegramBotUsername);
			this.account = loginResult.account;
			this.username = loginResult.username;
			this.userid = loginResult.userid;
			this.trusted = new MornyTrusted(this);
			StringBuilder trustedReadersDinnerIds = new StringBuilder();
			trusted.getTrustedReadersOfDinnerSet().forEach(id -> trustedReadersDinnerIds.append("\n    ").append(id));
			logger.info(String.format("""
					trusted param set:
					- master (id)
					    %d
					- trusted chat (id)
					    %d
					- trusted reader-of-dinner (id)%s""",
					config.trustedMaster, config.trustedChat, trustedReadersDinnerIds
			));
		}
		catch (Exception e) {
			RuntimeException ex = new RuntimeException("Cannot login to bot/api. :\n  " + e.getMessage());
			logger.error(ex.getMessage());
			throw ex;
		}
		
		this.extraActionInstance = ExtraAction.as(account);
		
		logger.info("Bot login succeed.");
		
	}
	
	/**
	 * 向外界暴露的 morny 初始化入口.
	 * <p>
	 * 如果 morny 已经初始化，则不会进行初始化，抛出错误消息并直接退出方法。
	 *
	 * @see #MornyCoeur 程序初始化方法
	 * @param config morny 实例的配置选项数据
	 */
	public static void init (MornyConfig config) {
		if (INSTANCE == null) {
			
			logger.info("Coeur Starting");
			INSTANCE = new MornyCoeur(config);
			
			MornyDaemons.start();
			
			logger.info("start telegram events listening");
			EventListeners.registerAllListeners();
			INSTANCE.account.setUpdatesListener(OnUpdate::onNormalUpdate);
			
			if (config.commandLoginRefresh) {
				logger.info("resetting telegram command list");
				commandManager().automaticUpdateList();
			}
			
			logger.info("Coeur start complete");
			return;
			
		}
		logger.error("Coeur already started!!!");
	}
	
	/**
	 * 向所有的数据管理器发起保存数据的指令
	 * @since 0.4.3.0
	 */
	public void saveDataAll () {
		TrackerDataManager.save();
	}
	
	/**
	 * 用于退出时进行缓存的任务处理等进行安全退出
	 */
	private void exitCleanup () {
		MornyDaemons.stop();
		if (config.commandLogoutClear) {
			commandManager.automaticRemoveList();
		}
	}
	
	/**
	 * 为程序在虚拟机上添加退出钩子
	 */
	private void configureSafeExit () {
		Runtime.getRuntime().addShutdownHook(new Thread(this::exitCleanup, "exit-cleaning"));
	}
	
	/**
	 * 登录 bot.
	 * <p>
	 * 会反复尝试三次进行登录。如果登录失败，则会直接抛出 RuntimeException 结束处理。
	 * 会通过 GetMe 动作验证是否连接上了 telegram api 服务器，
	 * 同时也要求登录获得的 username 和 {@link #username} 声明值相等
	 *
	 * @param api bot client 将会连接到的 telegram bot api 位置。
	 *            填入 {@code null} 则使用默认的 {@code "https://api.telegram.org/bot"}
	 * @param api4File bot client 将会连接到的 telegram file api 位置。
	 *                 如果传入 {@code null} 则会跟随 {@param api} 选项的设定（具体为在 {@param api} 路径的后面添加 {@code /file} 路径）。
	 *                 如果两者都为 {@code null}，则跟随默认的 {@value FileApi#FILE_API}
	 * @param key bot 的 api-token. 必要值
	 * @param requireName 要求登录到的需要的 username，如果登陆后的 username 与此不同则会报错退出。
	 *                    填入 {@code null} 则表示不对 username 作要求
	 * @return 成功登录后的 {@link TelegramBot} 对象
	 */
	@Nonnull
	private static LogInResult login (
			@Nullable String api, @Nullable String api4File,
			@Nonnull String key, @Nullable String requireName
	) {
		final TelegramBot.Builder accountConfig = new TelegramBot.Builder(key);
		boolean isCustomApi = false;
		String apiUrlSet = "https://api.telegram.org/bot";
		String api4FileUrlSet = FileApi.FILE_API;
		if (api != null) {
			api = api.endsWith("/") ? api.substring(0, api.length() - 1) : api;
			accountConfig.apiUrl(apiUrlSet = api.endsWith("/bot")? api : api + "/bot");
			isCustomApi = true;
		}
		if (api4File != null) {
			api4File = api4File.endsWith("/") ? api4File : api4File + "/";
			accountConfig.fileApiUrl(api4FileUrlSet = api4File.endsWith("/file/bot")? api4File : api4File + "/file/bot");
			isCustomApi = true;
		} else if (api != null && !api.endsWith("/bot")) {
			accountConfig.fileApiUrl(api4FileUrlSet = api + "/file/bot");
		}
		if (isCustomApi) {
			logger.info(String.format("""
					Telegram Bot API set to :
					- %s
					- %s""",
					apiUrlSet, api4FileUrlSet
			));
		}
		final TelegramBot account = accountConfig.build();
		logger.info("Trying to login...");
		for (int i = 1; i < 4; i++) {
			if (i != 1) logger.info("retrying...");
			try {
				final User remote = account.execute(new GetMe()).user();
				if (requireName != null && !requireName.equals(remote.username()))
					throw new RuntimeException("Required the bot @" + requireName + " but @" + remote.username() + " logged in!");
				logger.info("Succeed login to @" + remote.username());
				return new LogInResult(account, remote.username(), remote.id());
			} catch (Exception e) {
				logger.error(Log.exceptionLog(e));
				logger.error("login failed.");
			}
		}
		throw new RuntimeException("Login failed..");
	}
	
	/**
	 * @see #saveDataAll()
	 * @since 0.4.3.0
	 */
	public static void callSaveData () {
		INSTANCE.saveDataAll();
		logger.info("done all save action.");
	}
	
	/**
	 * 检查 Coeur 是否已经完成初始化.
	 * @since 1.0.0-alpha5
	 */
	public static boolean available() {
		return INSTANCE != null;
	}
	
	/**
	 * 获取登录成功后的 telegram bot 对象
	 *
	 * @return {@link #account MornyCoeur.account}
	 */
	@Nonnull
	public static TelegramBot getAccount () {
		return INSTANCE.account;
	}
	
	/**
	 * 获取登录 bot 的 username
	 *
	 * @return {@link #username MornyCoeur.username}
	 */
	@Nonnull
	public static String getUsername () {
		return INSTANCE.username;
	}
	
	/**
	 * 获取当前 morny 的配置数据
	 *
	 * @return {@link #config MornyCoeur.config}
	 */
	@Nonnull
	public static MornyConfig config () {
		return INSTANCE.config;
	}
	
	/**
	 * 获取 Morny 的{@link MornyTrusted 信任验证机}
	 *
	 * @return {@link #trusted MornyCoeur.trusted}
	 */
	@Nonnull
	public static MornyTrusted trustedInstance () {
		return INSTANCE.trusted;
	}
	
	@Nonnull
	public static MornyCommands commandManager () {
		return INSTANCE.commandManager;
	}
	
	@Nonnull
	public static MornyQueries queryManager () {
		return INSTANCE.queryManager;
	}
	
	@Nonnull
	public static ExtraAction extra () {
		return INSTANCE.extraActionInstance;
	}
	
	public static long getUserid () { return INSTANCE.userid; }
	
	public static void exit (int status, Object reason) {
		INSTANCE.whileExitReason = reason;
		System.exit(status);
	}
	
	public static Object getExitReason () {
		return INSTANCE.whileExitReason;
	}
	
}
