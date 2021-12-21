package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.CommonFormatUtils;

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
	
	private static boolean versionEchoMode = false;
	private static boolean welcomeEchoMode = false;
	
	private static boolean showWelcome = true;
	
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
	 *         {@code --no-hello} 不在主程序启动时输出用于欢迎消息的字符画。
	 *         与 {@code --only-hello} 参数不兼容 —— 会导致程序完全没有任何输出
	 *     </li>
	 *     <li>
	 *         {@code --outdated-block} 会使得 {@link MornyCoeur#latestEventTimestamp}
	 *         赋值为程序启动的时间，从而造成阻挡程序启动之前的消息事件处理效果。
	 *     </li>
	 * </ul>
	 * 除去选项之外，第一个参数会被赋值为 bot 的 telegram bot api token，
	 * 第二个参数会被赋值为 bot 的 username 限定名。其余的参数会被认定为无法理解。<br>
	 * <br>
	 * <b>自 {@code 0.4.2.3}，token 和 username 的赋值已被选项组支持</b><br>
	 * 使用参数所进行取值的 token 和 username 已被转移至 {@code --token} 和 {@code --username} 参数，
	 * <u>或许，直接参数赋值的支持将计划在 {@code 0.4.3} 标记废弃并在 {@code 0.5} 删除。</u>
	 * <s>但实际上这并不影响现在的使用，选项赋值目前仍属于测试功能</s><br>
	 * <b>但请勿混用</b>，这将使两个赋值出现混淆并<b>产生不可知的结果</b>
	 *
	 * @see MornyCoeur#main(String, String, long, long, long)
	 * @since 0.4.0.0
	 * @param args 参数组
	 */
	public static void main (@Nonnull String[] args) {
		
		String key = null;
		String username = null;
		boolean outdatedBlock = false;
		long master = 793274677L;
		long trustedChat = -1001541451710L;
		
		for (int i = 0; i < args.length; i++) {
			
			if (args[i].startsWith("-")) {
				
				switch (args[i]) {
					case "--outdated-block" -> {
						outdatedBlock = true;
						continue;
					}
					case "--no-hello" -> {
						showWelcome = false;
						continue;
					}
					case "--only-hello" -> {
						welcomeEchoMode = true;
						continue;
					}
					case "--version" -> {
						versionEchoMode = true;
						continue;
					}
					case "--token" -> {
						i++;
						key = args[i];
						continue;
					}
					case "--username" -> {
						i++;
						username = args[i];
						continue;
					}
					case "--master" -> {
						i++;
						master = Long.parseLong(args[i]);
						continue;
					}
					case "--trusted-chat" -> {
						i++;
						trustedChat = Long.parseLong(args[i]);
						continue;
					}
				}
				
			} else {
				
				if (key == null) {
					key = args[i];
					continue;
				}
				if (username == null) {
					username = args[i];
					continue;
				}
				
			}
			
			logger.warn("Can't understand arg to some meaning :\n  " + args[i]);
			
		}
		
		if (versionEchoMode) {
			
			logger.info(String.format("""
					Morny Cono Version
					- version :
					    %s
					- md5hash :
					    %s
					- co.time :
					    %d
					    %s [UTC]""",
					MornySystem.VERSION,
					MornySystem.getJarMd5(),
					GradleProjectConfigures.COMPILE_TIMESTAMP,
					CommonFormatUtils.formatDate(GradleProjectConfigures.COMPILE_TIMESTAMP, 0)
			));
			return;
			
		}
		
		if (showWelcome) logger.info(MornyHello.MORNY_PREVIEW_IMAGE_ASCII);
		if (welcomeEchoMode) return;
		
		assert key != null;
		MornyCoeur.main(key, username, master, trustedChat, outdatedBlock?System.currentTimeMillis():0);
		
	}
	
}
