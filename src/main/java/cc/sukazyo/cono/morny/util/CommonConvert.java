package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnull;

public class CommonConvert {
	
	private final static String[] hexArray = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
	
	/**
	 * 将字节数组转换成十六进制，并以字符串的形式返回
	 * 128位是指二进制位。二进制太长，所以一般都改写成16进制，
	 * 每一位16进制数可以代替4位二进制数，所以128位二进制数写成16进制就变成了128/4=32位。
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
	 * 将一个字节转换成十六进制，并以字符串的形式返回
	 */
	@Nonnull
	public static String byteToHex(byte b) {
		int n = b;
		if (n < 0)
			n = n + 256;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexArray[d1]+hexArray[d2];
	}
	
}
