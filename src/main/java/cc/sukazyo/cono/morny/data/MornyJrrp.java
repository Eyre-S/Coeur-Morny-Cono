package cc.sukazyo.cono.morny.data;

import cc.sukazyo.cono.morny.util.EncryptUtils;
import com.pengrad.telegrambot.model.User;

/**
 * Morny 的 jrrp 运算类.
 *
 * @see #getJrrpFromTelegramUser(User,long)
 * @see #calcJrrpXmomi(long,long)
 * @since 0.4.2.9
 */
public class MornyJrrp {
	
	public static double getJrrpFromTelegramUser (User user, long timestamp) {
		return calcJrrpXmomi(user.id(), timestamp / (1000 * 60 * 60 * 24)) * 100.0;
	}
	
	public static double calcJrrpXmomi (long userId, long dayStamp) {
		return (double)Long.parseLong(EncryptUtils.encryptByMD5(userId + "@" + dayStamp).substring(0, 4), 16) / (double)0xffff;
	}
	
}
