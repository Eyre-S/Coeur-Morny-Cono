package cc.sukazyo.cono.morny;

public class Logger {
	
	public static final Logger logger = new Logger();
	
	public void info(String message) {
		System.out.println(
				"[INFO]" +
				message.replaceAll("\\n", "\n[INFO]")
		);
	}
	
	public void warn (String message) {
		waring(message);
	}
	
	public void waring (String message) {
		System.out.println(
				"[WARN]" +
				message.replaceAll("\\n", "\n[WARN]")
		);
	}
	
}
