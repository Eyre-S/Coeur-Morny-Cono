package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnegative;
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
	
	/**
	 * 将一个字符串数组按照一定规则连接.
	 * <p>
	 * 连接的方式类似于"数据1+分隔符+数据2+分隔符+...+数据n-1+分隔符+数据n"
	 *
	 * @param array 需要进行连接的字符串数组，数组中每一个元素会是一个数据
	 * @param connector 在每两个传入数据中插入的分隔符
	 * @param startIndex 从传入的数据组中的哪一个位置开始（第一个元素的位置是 {@code 0}）
	 * @param stopIndex 从传入的数据组中的哪一个位置停止（元素位置计算方式同上）
	 * @return 连接好的字符串
	 */
	@Nonnull
	public static String stringsConnecting (
			@Nonnull String[] array, @Nonnull String connector, @Nonnegative int startIndex, @Nonnegative int stopIndex
	) {
		final StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < stopIndex; i++) {
			builder.append(array[i]);
			builder.append(connector);
		}
		builder.append(array[stopIndex]);
		return builder.toString();
	}
	
}
