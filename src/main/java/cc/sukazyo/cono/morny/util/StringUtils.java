package cc.sukazyo.cono.morny.util;

import java.util.Arrays;

public class StringUtils {
	
	public static String repeatChar (char c, int i) {
		char[] chars = new char[i];
		Arrays.fill(chars, c);
		return new String(chars);
	}
	
}
