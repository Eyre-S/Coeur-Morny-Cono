package cc.sukazyo.cono.morny.bot.event;

/**
 * @since 0.4.2.0
 */
public class OnEventHackHandle {
	
	/**
	 * @since 0.4.2.0
	 */
	public enum HackType {
		USER, GROUP, ANY
	}
	
	/**
	 * @since 0.4.2.0
	 */
	public static void registerHack(long fromMessageId, long fromChatId, long fromUserId,HackType type) {
	}
	
}
