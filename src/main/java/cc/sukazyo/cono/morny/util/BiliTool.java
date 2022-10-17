package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class BiliTool {
	
	private static final long V_CONV_XOR = 177451812L;
	private static final long V_CONV_ADD = 8728348608L;
	private static final char[] BV_TABLE = "fZodR9XQDSUm21yCkr6zBqiveYah8bt4xsWpHnJE7jL5VG3guMTKNPAwcF".toCharArray();
	private static final int TABLE_INT = BV_TABLE.length;
	private static final Map<Character, Integer> BV_TABLE_REVERSED = new HashMap<>();
	static { for (int i = 0; i < BV_TABLE.length; i++) BV_TABLE_REVERSED.put(BV_TABLE[i], i); }
	private static final char[] BV_TEMPLATE = "1  4 1 7  ".toCharArray();
	private static final int[] BV_TEMPLATE_FILTER = new int[]{9, 8, 1, 6, 2, 4};
	
	@Nonnegative
	public static long toAv (@Nonnull String bv) {
		long av = 0;
		for (int i = 0; i < BV_TEMPLATE_FILTER.length; i++) {
			av += BV_TABLE_REVERSED.get(bv.charAt(BV_TEMPLATE_FILTER[i])) * Math.pow(TABLE_INT,i);
		}
		return (av-V_CONV_ADD)^V_CONV_XOR;
	}
	
	@Nonnull
	public static String toBv (@Nonnegative long av) {
		av = (av^V_CONV_XOR)+V_CONV_ADD;
		final char[] bv = BV_TEMPLATE.clone();
		for (int i = 0; i < BV_TEMPLATE_FILTER.length; i++) {
			bv[BV_TEMPLATE_FILTER[i]] = BV_TABLE[(int)(Math.floor(av/(Math.pow(TABLE_INT, i)))%TABLE_INT)];
		}
		return String.copyValueOf(bv);
	}
	
}
