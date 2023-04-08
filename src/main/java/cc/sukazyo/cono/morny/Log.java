package cc.sukazyo.cono.morny;

import cc.sukazyo.messiva.formatter.SimpleFormatter;
import cc.sukazyo.messiva.log.LogLevel;
import cc.sukazyo.messiva.logger.Logger;
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
	public static final Logger logger = new Logger(new ConsoleAppender(new SimpleFormatter())).minLevel(LogLevel.INFO);
	
	/**
	 * Is the Debug mode enabled.
	 *
	 * @return if the minimal log level is equal or lower than DEBUG level.
	 */
	public static boolean debug () {
		return logger.levelSetting.minLevel().level <= LogLevel.DEBUG.level;
	}
	
	/**
	 * Switch the Debug log output enabled.
	 * <p>
	 * if enable the debug log output, all the Log regardless of LogLevel will be output.
	 * As default, if the debug log output is disabled, Logger will ignore the Logs level lower than INFO.
	 *
	 * @param debug switch enable the debug log output as true, or disable it as false.
	 */
	public static void debug (boolean debug) {
		if (debug) logger.minLevel(LogLevel.ALL);
		else logger.minLevel(LogLevel.INFO);
	}
	
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
