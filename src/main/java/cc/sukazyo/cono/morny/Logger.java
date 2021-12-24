package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.StringUtils;

public class Logger {
	
	public static final Logger logger = new Logger();
	
	public void info(String message) {
		System.out.println(formatMessage(message, "INFO"));
	}
	
	public void warn (String message) {
		waring(message);
	}
	
	public void waring (String message) {
		System.out.println(formatMessage(message, "WARN"));
	}
	
	public void error (String message) {
		System.out.println(formatMessage(message, "ERRO"));
	}
	
	private String formatMessage (String message, String level) {
		String prompt = String.format("[%s][%s]", System.currentTimeMillis(), Thread.currentThread().getName());
		String levelStr = String.format("[%s]", level);
		String newline = "\n" + StringUtils.repeatChar('\'', prompt.length()) + levelStr;
		return prompt + levelStr + message.replaceAll("\\n", newline);
	}
	
}
