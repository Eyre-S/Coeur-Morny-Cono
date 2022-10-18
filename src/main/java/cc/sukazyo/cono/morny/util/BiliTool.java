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
	
	/**
	 * Convert a <a href="https://www.bilibili.com/">Bilibili</a> AV video id format to BV id format.
	 * <p>
	 * the AV id is a number; the BV id is a special base58 number, it shows as String in programming.<br>
	 * eg:<br>
	 * while the link <i>{@code https://www.bilibili.com/video/BV17x411w7KC/}</i>
	 * shows the same with <i>{@code https://www.bilibili.com/video/av170001/}</i>,
	 * the AV id is <u>{@code 170001}</u>, the BV id is <u>{@code 17x411w7KC}</u>
	 * <p>
	 * for now , the BV id has 10 digits.
	 * the method <b>available while the <u>av-id < 2^27</u></b>, while it theoretically available when the av-id < 2^30.
	 *
	 * @see <a href="https://www.zhihu.com/question/381784377/answer/1099438784">mcfx的回复: 如何看待 2020 年 3 月 23 日哔哩哔哩将稿件的「av 号」变更为「BV 号」？</a>
	 *
	 * @param bv the BV id, a string in (a special) base58 number format, <b>without "BV" prefix</b>.
	 * @return the AV id corresponding to this bv id in <a href="https://www.bilibili.com/">Bilibili</a>, formatted as a number.
	 */
	@Nonnegative
	public static long toAv (@Nonnull String bv) {
		long av = 0;
		for (int i = 0; i < BV_TEMPLATE_FILTER.length; i++) {
			av += BV_TABLE_REVERSED.get(bv.charAt(BV_TEMPLATE_FILTER[i])) * Math.pow(TABLE_INT,i);
		}
		return (av-V_CONV_ADD)^V_CONV_XOR;
	}
	
	/**
	 * Convert a <a href="https://www.bilibili.com/">Bilibili</a> BV video id format to AV id format.
	 * <p>
	 * the AV id is a number; the BV id is a special base58 number, it shows as String in programming.<br>
	 * eg:<br>
	 * while the link <i>{@code https://www.bilibili.com/video/BV17x411w7KC/}</i>
	 * shows the same with <i>{@code https://www.bilibili.com/video/av170001/}</i>,
	 * the AV id is <u>{@code 170001}</u>, the BV id is <u>{@code 17x411w7KC}</u>
	 * <p>
	 * for now , the BV id has 10 digits.
	 * the method <b>available while the <u>av-id < 2^27</u></b>, while it theoretically available when the av-id < 2^30.
	 *
	 * @see <a href="https://www.zhihu.com/question/381784377/answer/1099438784">mcfx的回复: 如何看待 2020 年 3 月 23 日哔哩哔哩将稿件的「av 号」变更为「BV 号」？</a>
	 *
	 * @param av the (base10) AV id.
	 * @return the AV id corresponding to this bv id in <a href="https://www.bilibili.com/">Bilibili</a>,
	 *         as a (special) base 58 number format <b>without "BV" prefix</b>.
	 */
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
