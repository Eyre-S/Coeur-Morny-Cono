package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 用于数据加密或编解码的工具类.
 * <p>
 * 出于 java std 中 Base64 的 {@link Base64.Encoder encode}/{@link Base64.Decoder decode} 十分好用，在此不再进行包装。
 */
public class CommonEncrypt {
	
	/**
	 * 在使用加密算法处理字符串时默认会使用的字符串编码.
	 * <p>
	 * Morny 使用 UTF-8 编码因为这是一般而言加解密工具的默认行为
	 */
	public static final Charset ENCRYPT_STANDARD_CHARSET = StandardCharsets.UTF_8;
	
	@Nonnull
	private static byte[] hashAsJavaMessageDigest(String algorithm, @Nonnull byte[] data) {
		try {
			return MessageDigest.getInstance(algorithm).digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
	}
	
	/**
	 * 取得数据的 md5 散列值.
	 *
	 * @param data byte 数组形式的数据体
	 * @return 二进制(byte数组)格式的数据的 md5 散列值
	 */
	@Nonnull
	public static byte[] hashMd5 (@Nonnull byte[] data) {
		return hashAsJavaMessageDigest("md5", data);
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
	
	/**
	 * 取得数据的 sha1 散列值.
	 *
	 * @param data byte 数组形式的数据体
	 * @return 二进制(byte数组)格式的数据的 sha1 散列值
	 */
	@Nonnull
	public static byte[] hashSha1 (@Nonnull byte[] data) {
		return hashAsJavaMessageDigest("sha1", data);
	}
	
	/**
	 * 取得一个字符串的 sha1 散列值.
	 * <p>
	 * 输入的字符串将会以 {@link #ENCRYPT_STANDARD_CHARSET 默认的 UTF-8} 编码进行解析
	 *
	 * @param originString 要进行散列的字符串
	 * @return 二进制(byte数组)格式的 sha1 散列值
	 */
	@Nonnull
	public static byte[] hashSha1 (String originString) {
		return hashMd5(originString.getBytes(ENCRYPT_STANDARD_CHARSET));
	}
	
	/**
	 * 取得数据的 sha256 散列值.
	 *
	 * @param data byte 数组形式的数据体
	 * @return 二进制(byte数组)格式的数据的 sha256 散列值
	 */
	@Nonnull
	public static byte[] hashSha256 (@Nonnull byte[] data) {
		return hashAsJavaMessageDigest("sha256", data);
	}
	
	/**
	 * 取得一个字符串的 sha256 散列值.
	 * <p>
	 * 输入的字符串将会以 {@link #ENCRYPT_STANDARD_CHARSET 默认的 UTF-8} 编码进行解析
	 *
	 * @param originString 要进行散列的字符串
	 * @return 二进制(byte数组)格式的 sha256 散列值
	 */
	@Nonnull
	public static byte[] hashSha256 (String originString) {
		return hashMd5(originString.getBytes(ENCRYPT_STANDARD_CHARSET));
	}
	
	/**
	 * 取得数据的 sha512 散列值.
	 *
	 * @param data byte 数组形式的数据体
	 * @return 二进制(byte数组)格式的数据的 sha512 散列值
	 */
	@Nonnull
	public static byte[] hashSha512 (@Nonnull byte[] data) {
		return hashAsJavaMessageDigest("md5", data);
	}
	
	/**
	 * 取得一个字符串的 sha512 散列值.
	 * <p>
	 * 输入的字符串将会以 {@link #ENCRYPT_STANDARD_CHARSET 默认的 UTF-8} 编码进行解析
	 *
	 * @param originString 要进行散列的字符串
	 * @return 二进制(byte数组)格式的 sha512 散列值
	 */
	@Nonnull
	public static byte[] hashSha512 (String originString) {
		return hashMd5(originString.getBytes(ENCRYPT_STANDARD_CHARSET));
	}
	
	@Nonnull
	public static String base64FilenameLint (String inputName) {
		if (inputName.endsWith(".b64")) {
			return inputName.substring(0, inputName.length()-".b64".length());
		} else if (inputName.endsWith(".b64.txt")) {
			return inputName.substring(0, inputName.length()-".b64.txt".length());
		} else if (inputName.endsWith(".base64")) {
			return inputName.substring(0, inputName.length()-".base64".length());
		} else if (inputName.endsWith(".base64.txt")) {
			return inputName.substring(0, inputName.length()-".base64.txt".length());
		} else {
			return inputName;
		}
	}
	
}
