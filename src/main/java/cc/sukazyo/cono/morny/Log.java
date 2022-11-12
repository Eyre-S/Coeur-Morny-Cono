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
	
	public static String exceptionLog (Exception e) {
		final StringWriter stackTrace = new StringWriter();
		e.printStackTrace(new PrintWriter(stackTrace));
		return stackTrace.toString();
	}
	
}
