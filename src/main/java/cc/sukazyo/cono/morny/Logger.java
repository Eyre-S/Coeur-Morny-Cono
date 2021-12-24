package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.StringUtils;

import javax.annotation.Nonnull;

/**
 * Morny 的简单控制台 log 记录器
 */
public class Logger {
	
	/** Morny 的控制台 logger 实例 */
	public static final Logger logger = new Logger();
	
	/**
	 * [INFO] 级别的 log 消息
	 * @see #formatMessage(String, String) 输出整理规则
	 * @param message 消息文本，支持多行
	 */
	public void info(@Nonnull String message) {
		System.out.println(formatMessage(message, "INFO"));
	}
	
	/**
	 * [WARN] 级别的 log 消息<br>
	 * {@link #warning(String)} 的重写
	 * @see #formatMessage(String, String) 输出整理规则
	 * @see #warning(String) 源方法
	 * @param message 消息文本，支持多行
	 */
	public void warn (@Nonnull String message) {
		warning(message);
	}
	
	/**
	 * [WARN] 级别的 log 消息
	 * @see #formatMessage(String, String) 输出整理规则
	 * @see #warn(String) 别名:warn
	 * @param message 消息文本，支持多行
	 */
	public void warning (@Nonnull String message) {
		System.out.println(formatMessage(message, "WARN"));
	}
	
	/**
	 * [ERRO] 级别的 log 消息
	 * @see #formatMessage(String, String) 输出整理规则
	 * @param message 消息文本，支持多行
	 */
	public void error (@Nonnull String message) {
		System.out.println(formatMessage(message, "ERRO"));
	}
	
	/**
	 * 将传入的消息和消息元数据重新整理为固定格式<br>
	 * <br>
	 * 这个方法会{@link System#currentTimeMillis() 获取当前时间}和{@link Thread#currentThread() 当前线程}的名称，
	 * 然后将数据整理为 {@code [<timestamp>][<thread-name>][<level>]<data>} 格式。<br>
	 * 如果消息是多行的，则每行的开头都会被加入 {@code [<timestamp>][<thread-name>][<level>]} 这样的前缀，
	 * 不过，前缀中 {@code [<timestamp>][<thread-name>]} 会被转换为等长的 {@code '} 字串。
	 * <br>
	 * 最终的 format 格式将会类似于以下的模样：<pre><code>
[1019284827][EVT388223][INFO]Something message got:
'''''''''''''''''''''''[INFO] - data source: 19773
'''''''''''''''''''''''[INFO] - message raw: noh2q0jwd9j-jn-9jq92-ed
	 * </code></pre>
	 *
	 * @param message 消息文本，支持多行
	 * @param level log级别，考虑到对齐，推荐使用四位窄字元
	 * @return 整理后的字符串
	 */
	@Nonnull
	private String formatMessage (@Nonnull String message, @Nonnull String level) {
		final String prompt = String.format("[%s][%s]", System.currentTimeMillis(), Thread.currentThread().getName());
		final String levelStr = String.format("[%s]", level);
		final String newline = "\n" + StringUtils.repeatChar('\'', prompt.length()) + levelStr;
		return prompt + levelStr + message.replaceAll("\\n", newline);
	}
	
}
