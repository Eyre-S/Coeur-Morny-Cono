package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用于数据加密或编解码的工具类.
 */
public class CommonEncrypt {
	
	/**
	 * 在使用加密算法处理字符串时默认会使用的字符串编码.
	 * <p>
	 * Morny 使用 UTF-8 编码因为这是一般而言加解密工具的默认行为
	 */
	public static final Charset ENCRYPT_STANDARD_CHARSET = StandardCharsets.UTF_8;
	
	/**
	 * 取得数据的 md5 散列值.
	 *
	 * @param data byte 数组形式的数据体
	 * @return 二进制(byte数组)格式的数据的 md5 散列值
	 */
	@Nonnull
	public static byte[] hashMd5 (@Nonnull byte[] data) {
		try {
			return MessageDigest.getInstance("md5").digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException();
		}
		
	}
	
	/**
	 * 取得一个字符串的 md5 散列值.
	 * <p>
	 * 输入的字符串将会以 {@link #ENCRYPT_STANDARD_CHARSET 默认的 UTF-8} 编码进行解析
	 *
	 * @param originString 要进行散列的字符串
	 * @return 二进制(byte数组)格式的 md5 散列值
	 */
	@Nonnull
	public static byte[] hashMd5 (String originString) {
		return hashMd5(originString.getBytes(ENCRYPT_STANDARD_CHARSET));
	}
	
}
