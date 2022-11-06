package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.CommonFormat;

import javax.annotation.Nonnull;

import static cc.sukazyo.cono.morny.Log.logger;

/**
 * 程序启动入口<br>
 * <br>
 * 会处理程序传入的参数和选项等数据，并执行对应的启动方式<br>
 *
 * @since 0.4.0.0
 */
public class ServerMain {
	
	public static final long systemStartupTime = System.currentTimeMillis();
	
	private static final String THREAD_MORNY_INIT = "morny-init";
	
	/**
	 * 程序入口，也是参数处理器<br>
	 * <br>
	 * 以 {@code -} 开头的参数会被解析为选项<br>
	 * <br>
	 * 支持以下选项
	 * <ul>
	 *     <li>
	 *         {@code --version} 只输出版本信息，不运行主程序。此参数会导致其它所有参数失效（优先级最高）
	 *     </li>
	 *     <li>
	 *         {@code --only-hello} 只输出欢迎字符画({@link MornyHello})，不运行主程序。
	 *         不要同时使用 {@code --no-hello}，原因见下。
	 *     </li>
	 *     <li>
	 *         {@code --token} <b>主程序模式的必选项</b><br>
	 *         用于 bot 启动的 telegram bot api token
	 *     </li>
	 *     <li>
	 *         {@code --username} {@link MornyCoeur#getUsername() bot 的 username} 预定义
	 *     </li>
	 *     <li>
	 *         {@code --api} 设定 {@link MornyCoeur#getAccount() bot client} 使用的 telegram bot api server。
	 *         需要注意的是如果带有后缀 {@code /bot} 则会单独设定 api server
	 *         而不会适应性的同时为 {@code --api-files} 设定值。
	 *     </li>
	 *     <li>
	 *         {@code --api-files} 单独设定 {@link MornyCoeur#getAccount() bot client} 使用的 telegram bot file api server
	 *     </li>
	 *     <li>
	 *         {@code --no-hello} 不在主程序启动时输出用于欢迎消息的字符画。
	 *         与 {@code --only-hello} 参数不兼容 —— 会导致程序完全没有任何输出
	 *     </li>
	 *     <li>
	 *         {@code --outdated-block} 会使得 {@link MornyConfig#eventIgnoreOutdated}
	 *         赋值为程序启动的时间，从而造成阻挡程序启动之前的消息事件处理效果。
	 *     </li>
	 *     <li>
	 *         {@code --auto-cmd} (下面两个)选项 {@code --auto-cmd-list} 和 {@code --auto-cmd-remove} 的合并版本
	 *     </li>
	 *     <li>
	 *         {@code --auto-cmd-list} 使 morny 在启动时自动依据程序本体更新登录 bot 的命令列表
	 *     </li>
	 *     <li>
	 *         {@code --auto-cmd-remove} 使 morny 在关闭时自动依据程序本体删除 bot 的命令列表
	 *     </li>
	 * </ul>
	 * <s>除去选项之外，第一个参数会被赋值为 bot 的 telegram bot api token，</s>
	 * <s>第二个参数会被赋值为 bot 的 username 限定名。其余的参数会被认定为无法理解。</s><br>
	 * <b>自 {@code 0.4.2.3}，token 和 username 的赋值已被选项组支持</b><br>
	 * <b>自 {@code 0.5.0.4}，旧的直接通过参数为 bot token & username 赋值的方式已被删除</b>
	 * 使用参数所进行取值的 token 和 username 已被转移至 {@code --token} 和 {@code --username} 参数<br>
	 *
	 * @see MornyCoeur#main
	 * @since 0.4.0.0
	 * @param args 参数组
	 */
	public static void main (@Nonnull String[] args) {
		
		//#
		//# 启动参数设置区块
		//#
		final MornyConfig.Prototype config = new MornyConfig.Prototype();
		boolean versionEchoMode = false;
		boolean welcomeEchoMode = false;
		boolean showWelcome = true;
		
		config.eventOutdatedTimestamp = systemStartupTime;
		
		for (int i = 0; i < args.length; i++) {
			
			if (args[i].startsWith("-")) {
				
				switch (args[i]) {
					case "--outdated-block", "-ob" -> {
						config.eventIgnoreOutdated = true;
						continue;
					}
					case "--no-hello", "-hf", "--quiet", "-q" -> {
						showWelcome = false;
						continue;
					}
					case "--only-hello", "-ho", "-o", "-hi" -> {
						welcomeEchoMode = true;
						continue;
					}
					case "--version", "-v" -> {
						versionEchoMode = true;
						continue;
					}
					case "--token", "-t" -> {
						i++;
						config.telegramBotKey = args[i];
						continue;
					}
					case "--username", "-u" -> {
						i++;
						config.telegramBotUsername = args[i];
						continue;
					}
					case "--master", "-mm" -> {
						i++;
						config.trustedMaster = Long.parseLong(args[i]);
						continue;
					}
					case "--trusted-chat", "-trs" -> {
						i++;
						config.trustedChat = Long.parseLong(args[i]);
						continue;
					}
					//noinspection SpellCheckingInspection
					case "--trusted-reader-dinner", "-trsd" -> {
						i++;
						config.dinnerTrustedReaders.add(Long.parseLong(args[i]));
						continue;
					}
					case "--auto-cmd", "-cmd", "-c" -> {
						config.commandLoginRefresh = true;
						config.commandLogoutClear = true;
						continue;
					}
					case "--auto-cmd-list", "-ca" -> {
						config.commandLoginRefresh = true;
						continue;
					}
					case "--auto-cmd-remove", "-cr" -> {
						config.commandLogoutClear = true;
						continue;
					}
					case "--api", "-a" -> {
						i++;
						config.telegramBotApiServer = args[i];
						continue;
					}
					case "--api-files", "files-api", "-af" -> {
						i++;
						config.telegramBotApiServer4File = args[i];
						continue;
					}
				}
				
			}
			
			logger.warn("Can't understand arg to some meaning :\n  " + args[i]);
			
		}
		
		String propToken = null;
		String propTokenKey = null;
		for (String iKey : MornyConfig.PROP_TOKEN_KEY) {
			if (System.getenv(iKey) != null) {
				propToken = System.getenv(iKey);
				propTokenKey = iKey;
			}
		}
		
		//#
		//# 启动相关参数的检查和处理
		//#
		
		if (versionEchoMode) {
			
			logger.info(String.format("""
					Morny Cono Version
					- version :
					    %s  %s
					- md5hash :
					    %s
					- co.time :
					    %d
					    %s [UTC]""",
					MornySystem.VERSION, MornySystem.CODENAME.toUpperCase(),
					MornySystem.getJarMd5(),
					BuildConfig.CODE_TIMESTAMP,
					CommonFormat.formatDate(BuildConfig.CODE_TIMESTAMP, 0)
			));
			return;
			
		}
		
		if (showWelcome) logger.info(MornyHello.MORNY_PREVIEW_IMAGE_ASCII);
		if (welcomeEchoMode) return;
		
		logger.info(String.format("""
				ServerMain.java Loaded >>>
				- version %s (%s)(%d)
				- Morny %s""",
				MornySystem.VERSION,
				MornySystem.getJarMd5(), BuildConfig.CODE_TIMESTAMP,
				MornySystem.CODENAME.toUpperCase()
		));
		
		//#
		//# Coeur 参数检查和正式启动主程序
		//#
		
		if (propToken != null) {
			config.telegramBotKey = propToken;
			logger.info("Parameter <token> set by EnvVar $"+propTokenKey);
		}
		
		Thread.currentThread().setName(THREAD_MORNY_INIT);
		try {
			MornyCoeur.main(new MornyConfig(config));
		} catch (MornyConfig.CheckFailure.NullTelegramBotKey ignore) {
			logger.info("Parameter required has no value:\n --token.");
		} catch (MornyConfig.CheckFailure e) {
			logger.error("Unknown failure occurred while starting ServerMain!:");
			e.printStackTrace(System.out);
		}
		
	}
	
}
