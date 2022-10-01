package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用于数据加密或编码的工具类<br>
 * <s>显然大部分代码是抄来的</s><br>
 * <ul>
 *     <li><a href="https://blog.csdn.net/yu540135101/article/details/86765457">{@link #encryptByMD5} 来源</a></li>
 * </ul>
 */
public class CommonEncrypt {
	
	public static final Charset ENCRYPT_STANDARD_CHARSET = StandardCharsets.UTF_8;
	
	/***
	 * 对指定的字符串进行MD5加密
	 */
	
	@Nonnull
	public static byte[] encryptByMD5(@Nonnull byte[] data) {
		try {
			return MessageDigest.getInstance("md5").digest(data);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException();
		}
		
	}
	@Nonnull
	public static byte[] encryptByMD5(String originString) {
		return encryptByMD5(originString.getBytes(ENCRYPT_STANDARD_CHARSET));
	}
	
}
