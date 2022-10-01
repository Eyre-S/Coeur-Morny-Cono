package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnull;

/**
 * 进行简单类型转换等工作的类.
 */
public class CommonConvert {
	
	/**
	 * 将字节数组转换成 hex 字符串.
	 * @param b 字节数组
	 * @return String 格式的字节数组的 hex 值（每个字节当中没有分隔符）
	 * @see #byteToHex(byte)
	 */
	@Nonnull
	public static String byteArrayToHex(@Nonnull byte[] b){
		StringBuilder sb = new StringBuilder();
		for (byte value : b) {
			sb.append(byteToHex(value));
		}
		return sb.toString();
	}
	
	/**
	 * 将一个字节转换成十六进制 hex 字符串.
	 * @param b 字节值
	 * @return String 格式的字节的 hex 值（小写）
	 */
	@Nonnull
	public static String byteToHex(byte b) {
		final String hex = Integer.toHexString(b & 0xff);
		return hex.length()<2?"0"+hex:hex;
	}
	
}
