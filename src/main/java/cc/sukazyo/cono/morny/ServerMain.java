package cc.sukazyo.cono.morny;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static cc.sukazyo.cono.morny.Logger.logger;

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
	 *         {@code --no-hello} 不在主程序启动时输出用于欢迎消息的字符画。
	 *         与 {@code --only-hello} 参数不兼容 —— 会导致程序完全没有任何输出
	 *     </li>
	 *     <li>
	 *         {@code --outdated-block} 会使得 {@link MornyCoeur#latestEventTimestamp}
	 *         赋值为程序启动的时间，从而造成阻挡程序启动之前的消息事件处理效果。
	 *     </li>
	 * </ul>
	 * 除去选项之外，第一个参数会被赋值为 bot 的 telegram bot api token，
	 * 第二个参数会被赋值为 bot 的 username 限定名。其余的参数会被认定为无法理解。
	 *
	 * @see MornyCoeur#main(String, String, long)
	 * @since 0.4.0.0
	 * @param args 参数组
	 */
	public static void main (@Nonnull String[] args) {
		
		String key = null;
		String username = null;
		boolean outdatedBlock = false;
		
		for (String arg : args) {
			
			if (arg.startsWith("-")) {
				
				switch (arg) {
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
				}
				
			} else {
				
				if (key == null) {
					key = arg;
					continue;
				}
				if (username == null) {
					username = arg;
					continue;
				}
				
			}
			
			logger.warn("Can't understand arg to some meaning :\n  " + arg);
			
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
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS").format(LocalDateTime.ofInstant(
							Instant.ofEpochMilli(GradleProjectConfigures.COMPILE_TIMESTAMP),
							ZoneId.ofOffset("UTC", ZoneOffset.UTC)))
			));
			return;
			
		}
		
		if (showWelcome) logger.info(MornyHello.MORNY_PREVIEW_IMAGE_ASCII);
		if (welcomeEchoMode) return;
		
		assert key != null;
		MornyCoeur.main(key, username, outdatedBlock?System.currentTimeMillis():0);
		
	}
	
}
