package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.StringUtils;

import javax.annotation.Nonnull;

public class Logger {
	
	public static final Logger logger = new Logger();
	
	public void info(@Nonnull String message) {
		System.out.println(formatMessage(message, "INFO"));
	}
	
	public void warn (@Nonnull String message) {
		waring(message);
	}
	
	public void waring (@Nonnull String message) {
		System.out.println(formatMessage(message, "WARN"));
	}
	
	public void error (@Nonnull String message) {
		System.out.println(formatMessage(message, "ERRO"));
	}
	
	@Nonnull
	private String formatMessage (@Nonnull String message, @Nonnull String level) {
		final String prompt = String.format("[%s][%s]", System.currentTimeMillis(), Thread.currentThread().getName());
		final String levelStr = String.format("[%s]", level);
		final String newline = "\n" + StringUtils.repeatChar('\'', prompt.length()) + levelStr;
		return prompt + levelStr + message.replaceAll("\\n", newline);
	}
	
}
