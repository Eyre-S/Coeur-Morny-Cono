package cc.sukazyo.cono.morny;

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
	
	private String formatMessage (String message, String level) {
		String levelStr = "\n["+level+"]";
		return String.format(
				"[%d][%s][%s]%s",
				System.currentTimeMillis(),
				Thread.currentThread().getName(),
				level,
				message.replaceAll("\\n", levelStr)
		);
	}
	
}
