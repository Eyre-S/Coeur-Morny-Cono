package cc.sukazyo.cono.morny;

import cc.sukazyo.messiva.Logger;
import cc.sukazyo.messiva.appender.ConsoleAppender;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Morny 的 log 管理器
 */
public class Log {
	
	/**
	 * Morny 的 Logger 实例，
	 * messiva 更新
	 * @since 0.4.1.1
	 */
	public static final Logger logger = new Logger(new ConsoleAppender());
	
	/**
	 * 获取异常的堆栈信息.
	 * @param e 异常体
	 * @return {@link String} 格式的异常的堆栈报告信息.
	 * @see 1.0.0-alpha5
	 */
	public static String exceptionLog (Exception e) {
		final StringWriter stackTrace = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTrace));
		return stackTrace.toString();
	}
	
}
