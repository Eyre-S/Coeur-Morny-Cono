package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.bot.api.OnUpdate;
import cc.sukazyo.cono.morny.bot.event.EventListeners;
import cc.sukazyo.cono.morny.data.tracker.TrackerDataManager;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.GetMe;

import javax.annotation.Nonnull;

import static cc.sukazyo.cono.morny.Logger.logger;

/**
 * Morny Cono 核心<br>
 * <br>
 * - 的程序化入口类，保管着 morny 的核心属性<br>
 */
public class MornyCoeur {
	
	/** morny 的 bot 账户 */
	private static TelegramBot account;
	/**
	 * morny 的 bot 账户的用户名<br>
	 * <br>
	 * 出于技术限制，这个字段目前是写死的
	 */
	public static final String USERNAME = "morny_cono_annie_bot";
	
	/**
	 * 程序入口<br>
	 * <br>
	 * 会从命令行参数取得初始化数据并初始化程序和bot<br>
	 * <br>
	 * - 第一个参数({@code args[0]})必序传递，值为 telegram-bot 的 api-token<br>
	 * - 第二个参数可选 {@code --no-hello} 和 {@code --only-hello}，
	 * 前者表示不输出{@link MornyHello#MORNY_PREVIEW_IMAGE_ASCII 欢迎标语}，
	 * 后者表示只输出{@link MornyHello#MORNY_PREVIEW_IMAGE_ASCII 欢迎标语}而不运行程序逻辑<br>
	 * <br>
	 * 或者，在第一个参数处使用 {@code --version} 来输出当前程序的版本信息
	 *
	 * @param args 程序命令行参数
	 */
	public static void main (@Nonnull String[] args) {
		
		if ("--version".equals(args[0])) {
			logger.info(String.format("""
					Morny Cono Version
					- version : %s
					- md5hash : %s
					""", MornySystem.VERSION, MornySystem.getJarMd5()
			));
			return;
		}
		
		if (!(args.length > 1 && "--no-hello".equals(args[1])))
			logger.info(MornyHello.MORNY_PREVIEW_IMAGE_ASCII);
		if (args.length > 1 && "--only-hello".equals(args[1]))
			return;
		logger.info("System Starting");
		
		configureSafeExit();
		
		logger.info("args key:\n  " + args[0]);
		
		try { account = login(args[0]); }
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
	 * 同时也要求登录获得的 username 和 {@link #USERNAME} 声明值相等
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
				if (!USERNAME.equals(username))
					throw new RuntimeException("Required the bot @"+USERNAME + " but @"+username + " logged in!");
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
	
}
