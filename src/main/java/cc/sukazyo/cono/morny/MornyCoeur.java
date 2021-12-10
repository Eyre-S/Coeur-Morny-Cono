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
	
	/** morny 的 bot 账户 */
	private static TelegramBot account;
	/**
	 * morny 的 bot 账户的用户名<br>
	 * <br>
	 * 这个字段将会在登陆成功后赋值为登录到的 bot 的 username。
	 * 它应该是和 {@link #account} 的 username 同步的<br>
	 * <br>
	 * 如果在登陆之前就定义了此字段，则登陆代码会验证登陆的 bot 的 username
	 * 是否与定义的 username 符合。如果不符合则会报错。
	 */
	private static String username;
	/**
	 * morny 的事件忽略前缀时间<br>
	 * <br>
	 * {@link cc.sukazyo.cono.morny.bot.event.OnUpdateTimestampOffsetLock}
	 * 会根据这里定义的时间戳取消掉比此时间更早的事件链
	 */
	public static long latestEventTimestamp;
	
	/**
	 * bot 启动入口，执行 bot 初始化
	 *
	 * @param botKey bot 的 telegram bot api token
	 * @param botUsername bot 的 username 限定。如果为 null 则表示不限定，
	 *                    如果指定，则登录时会检查所登陆的 bot 的用户名是否与此相等
	 * @param latestEventTimestamp 事件处理器会处理事件的最早时间戳 ——
	 *                             只有限定的 message 事件会受此影响。
	 *                             单位为毫秒
	 */
	public static void main (@Nonnull String botKey, @Nullable String botUsername, long latestEventTimestamp) {
		
		MornyCoeur.latestEventTimestamp = latestEventTimestamp;
		
		logger.info("System Starting");
		
		configureSafeExit();
		
		logger.info("args key:\n  " + botKey);
		if (botUsername != null) {
			logger.info("login as:\n  " + botUsername);
		}
		
		try { account = login(botKey); }
		catch (Exception e) { logger.error("Cannot login to bot/api. :\n  " + e.getMessage()); System.exit(-1); }
		
		logger.info("Bot login succeed.");
		
		TrackerDataManager.init();
		EventListeners.registerAllListeners();
		account.setUpdatesListener(OnUpdate::onNormalUpdate);
		
		logger.info("System start complete");
		
	}
	
	/**
	 * 用于退出时进行缓存的任务处理等进行安全退出
	 */
	private static void exitCleanup () {
		TrackerDataManager.DAEMON.interrupt();
		TrackerDataManager.trackingLock.lock();
	}
	
	/**
	 * 为程序在虚拟机上添加退出钩子
	 */
	private static void configureSafeExit () {
		Runtime.getRuntime().addShutdownHook(new Thread(MornyCoeur::exitCleanup));
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
	public static TelegramBot login (@Nonnull String key) {
		final TelegramBot account = new TelegramBot(key);
		logger.info("Trying to login...");
		for (int i = 1; i < 4; i++) {
			if (i != 1) logger.info("retrying...");
			try {
				final String username = account.execute(new GetMe()).user().username();
				if (MornyCoeur.username != null && !MornyCoeur.username.equals(username))
					throw new RuntimeException("Required the bot @" + MornyCoeur.username + " but @" + username + " logged in!");
				else MornyCoeur.username = username;
				logger.info("Succeed login to @" + username);
				return account;
			} catch (Exception e) {
				e.printStackTrace(System.out);
				logger.error("login failed.");
			}
		}
		throw new RuntimeException("Login failed..");
	}
	
	/**
	 * 获取登录成功后的 telegram bot 对象
	 *
	 * @return {@link #account MornyCoeur.account}
	 */
	public static TelegramBot getAccount () {
		return account;
	}
	
	/**
	 * 获取登录 bot 的 username
	 *
	 * @return {@link #username MornyCoeur.username}
	 */
	public static String getUsername () {
		return username;
	}
	
}
