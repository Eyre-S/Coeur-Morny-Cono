package cc.sukazyo.cono.morny.data;

import cc.sukazyo.cono.morny.util.CommonConvert;
import cc.sukazyo.cono.morny.util.CommonEncrypt;
import com.pengrad.telegrambot.model.User;

/**
 * Morny 的 jrrp 运算类.
 *
 * @see #getJrrpFromTelegramUser(User,long)
 * @see #calcJrrpXmomi(long,long)
 * @since 0.4.2.9
 */
public class MornyJrrp {
	
	/**
	 * 通过 telegram 用户和时间戳作为参数获取 jrrp.
	 *
	 * @see #calcJrrpXmomi  当前版本的实现算法 {@code Xmomi}
	 * @since 0.4.2.9
	 * @param user telegram 用户
	 * @param timestamp 时间戳
	 * @return 通过当前版本的算法计算出的用户 jrrp 值，取值为 {@code [0.00, 100.00]}
	 */
	public static double getJrrpFromTelegramUser (User user, long timestamp) {
		return calcJrrpXmomi(user.id(), timestamp / (1000 * 60 * 60 * 24)) * 100.0;
	}
	
	/**
	 * {@code Xmomi} 版本的 jrrp 算法.
	 * <p>
	 * 算法规则为，将用户id与日期戳链接为 <u><code>uid@daystamp</code></u> 这样的字符串，
	 * 然后通过 MD5 计算出字符串的哈希值，取哈希值前4个字节，将其作为16进制数值表示法转换为取值为 {@code [0x0000, 0xffff]} 的数值，
	 * 得到的数值除以区间最大值 {@code 0xffff} 即可得到一个分布在 {@code [0.0, 1.0]} 之间的分布值，
	 * 这个分布值乘以 {@code 100.0}，即为计算得到的 jrrp 数值。
	 *
	 * @since 0.4.2.9
	 * @param userId telegram 用户 uid
	 * @param dayStamp unix 时间戳转换为日期单位后的数值. 数值应该在转换前转换时区
	 * @return 算法得到的 jrrp 值，取值为 {@code [0.00. 100.00]}
	 */
	public static double calcJrrpXmomi (long userId, long dayStamp) {
		return (double)Long.parseLong(CommonConvert.byteArrayToHex(CommonEncrypt.hashMd5(userId + "@" + dayStamp)).substring(0, 4), 16) / (double)0xffff;
	}
	
}
