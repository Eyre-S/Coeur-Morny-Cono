package cc.sukazyo.cono.morny.util;

import java.util.ArrayList;
import java.util.Arrays;

public class StringUtils {
	
	public static String repeatChar (char c, int i) {
		char[] chars = new char[i];
		Arrays.fill(chars, c);
		return new String(chars);
	}
	
	public static String[] formatCommand (String com) {
		
		ArrayList<String> arr = new ArrayList<>();
		
		StringBuilder tmp = new StringBuilder();
		char[] coma = com.toCharArray();
		for (int i = 0; i < coma.length; i++) {
			if (coma[i] == ' ') {
				if (!tmp.toString().equals("")) { arr.add(tmp.toString()); }
				tmp.setLength(0);
			} else if (coma[i] == '"') {
				while (true) {
					i++;
					if (coma[i] == '"') {
						break;
					} else if (coma[i] == '\\' && (coma[i+1] == '"' || coma[i+1] == '\\')) {
						i++;
						tmp.append(coma[i]);
					} else {
						tmp.append(coma[i]);
					}
				}
			} else if (coma[i] == '\\' && (coma[i+1] == ' ' || coma[i+1] == '"' || coma[i+1] == '\\')) {
				i++;
				tmp.append(coma[i]);
			} else {
				tmp.append(coma[i]);
			}
		}
		if (!tmp.toString().equals("")) { arr.add(tmp.toString()); }
		tmp.setLength(0);
		
		String[] out = new String[arr.size()];
		arr.toArray(out);
		return out;
		
	}
	
	public static String connectStringArray (String[] array, String connector, int startIndex, int stopIndex) {
		StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < stopIndex; i++) {
			builder.append(array[i]);
			builder.append(connector);
		}
		builder.append(array[stopIndex]);
		return builder.toString();
	}
	
}
