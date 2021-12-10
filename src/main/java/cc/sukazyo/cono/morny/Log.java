package cc.sukazyo.cono.morny;

import cc.sukazyo.messiva.Logger;
import cc.sukazyo.messiva.appender.ConsoleAppender;

/**
 * Morny 的简单控制台 log 记录器
 */
public class Log {
	
	/** Morny 的 Logger 实例 */
	public static final Logger logger = new Logger(new ConsoleAppender());
	
}
