package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.bot.api.OnUpdate;
import cc.sukazyo.cono.morny.bot.event.EventListeners;
import cc.sukazyo.cono.morny.data.tracker.TrackerDataManager;
import com.pengrad.telegrambot.TelegramBot;
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
	
	/** 当前 Morny 的{@link MornyTrusted 信任验证机}实例 */
	private final MornyTrusted trusted;
	
	/** morny 的 bot 账户 */
	private final TelegramBot account;
	/**
	 * morny 的 bot 账户的用户名<br>
	 * <br>
	 * 这个字段将会在登陆成功后赋值为登录到的 bot 的 username。
	 * 它应该是和 {@link #account} 的 username 同步的<br>
	 * <br>
	 * 如果在登陆之前就定义了此字段，则登陆代码会验证登陆的 bot 的 username
	 * 是否与定义的 username 符合。如果不符合则会报错。
	 */
	private final String username;
	/**
	 * morny 的事件忽略前缀时间<br>
	 * <br>
	 * {@link cc.sukazyo.cono.morny.bot.event.OnUpdateTimestampOffsetLock}
	 * 会根据这里定义的时间戳取消掉比此时间更早的事件链
	 */
	public long latestEventTimestamp;
	/**
	 * morny 主程序启动时间<br>
	 * 用于统计数据
	 */
	public static final long coeurStartTimestamp = System.currentTimeMillis();
	
	private record LogInResult(TelegramBot account, String username) { }
	
	/**
	 * 执行 bot 初始化
	 *
	 * @param botKey bot 的 telegram bot api token
	 * @param botUsername bot 的 username 限定。如果为 null 则表示不限定，
	 *                    如果指定，则登录时会检查所登陆的 bot 的用户名是否与此相等
	 * @param master morny 实例所信任的主人的 id。用于初始化 {@link #trusted}
	 * @param trustedChat morny 实例所信任的群组的 id。用于初始化 {@link #trusted}
	 * @param latestEventTimestamp 事件处理器会处理事件的最早时间戳 ——
	 *                             只有限定的 message 事件会受此影响。
	 *                             单位为毫秒
	 */
	private MornyCoeur (
			@Nonnull String botKey, @Nullable String botUsername,
			long master, long trustedChat, long latestEventTimestamp
	) {
		
		this.latestEventTimestamp = latestEventTimestamp;
		configureSafeExit();
		
		logger.info("args key:\n  " + botKey);
		if (botUsername != null) {
			logger.info("login as:\n  " + botUsername);
		}
		
		try {
			final LogInResult loginResult = login(botKey);
			this.account = loginResult.account;
			this.username = loginResult.username;
			this.trusted = new MornyTrusted(master, trustedChat);
			logger.info(String.format("""
					trusted param set:
					- master (id)
					    %d
					- trusted chat (id)
					    %d""",
					master, trustedChat
			));
		}
		catch (Exception e) {
			RuntimeException ex = new RuntimeException("Cannot login to bot/api. :\n  " + e.getMessage());
			logger.error(ex.getMessage());
			throw ex;
		}
		
		logger.info("Bot login succeed.");
		
	}
	
	/**
	 * 向外界暴露的 morny 初始化入口.
	 * <p>
	 * 如果 morny 已经初始化，则不会进行初始化，抛出错误消息并直接退出方法。
	 *
	 * @see #MornyCoeur 程序初始化方法
	 */
	public static void main (
			@Nonnull String botKey, @Nullable String botUsername,
			long master, long trustedChat, long latestEventTimestamp
	) {
		if (INSTANCE == null) {
			logger.info("System Starting");
			INSTANCE = new MornyCoeur(botKey, botUsername, master, trustedChat, latestEventTimestamp);
			TrackerDataManager.init();
			EventListeners.registerAllListeners();
			INSTANCE.account.setUpdatesListener(OnUpdate::onNormalUpdate);
			logger.info("System start complete");
			return;
		}
		logger.error("System already started coeur!!!");
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
		TrackerDataManager.DAEMON.interrupt();
		TrackerDataManager.trackingLock.lock();
	}
	
	/**
	 * 为程序在虚拟机上添加退出钩子
	 */
	private void configureSafeExit () {
		Runtime.getRuntime().addShutdownHook(new Thread(this::exitCleanup));
	}
	
	/**
	 * 登录 bot<br>
	 * <br>
	 * 会反复尝试三次进行登录。如果登录失败，则会直接抛出 RuntimeException 结束处理。
	 * 会通过 GetMe 动作验证是否连接上了 telegram api 服务器，
	 * 同时也要求登录获得的 username 和 {@link #username} 声明值相等
	 *
	 * @param key bot 的 api-token
	 * @return 成功登录后的 {@link TelegramBot} 对象
	 */
	@Nonnull
	private LogInResult login (@Nonnull String key) {
		final TelegramBot account = new TelegramBot(key);
		logger.info("Trying to login...");
		for (int i = 1; i < 4; i++) {
			if (i != 1) logger.info("retrying...");
			try {
				final String username = account.execute(new GetMe()).user().username();
				if (this.username != null && !this.username.equals(username))
					throw new RuntimeException("Required the bot @" + this.username + " but @" + username + " logged in!");
				logger.info("Succeed login to @" + username);
				return new LogInResult(account, username);
			} catch (Exception e) {
				e.printStackTrace(System.out);
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
	 *
	 * 获取忽略时间点
	 *
	 * @return {@link #latestEventTimestamp MornyCoeur.latestEventTimestamp}
	 */
	public static long getLatestEventTimestamp () {
		return INSTANCE.latestEventTimestamp;
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
	
}
