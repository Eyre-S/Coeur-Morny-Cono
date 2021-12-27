package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;

public class StringUtils {
	
	@Nonnull
	public static String[] formatCommand (@Nonnull String com) {
		
		final ArrayList<String> arr = new ArrayList<>();
		
		final StringBuilder tmp = new StringBuilder();
		final char[] coma = com.toCharArray();
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
		
		final String[] out = new String[arr.size()];
		arr.toArray(out);
		return out;
		
	}
	
	@Nonnull
	public static String connectStringArray (
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
	
	@Nonnull
	public static String escapeHtmlTelegram (String raw) {
		raw = raw.replaceAll("&", "&amp;");
		raw = raw.replaceAll("<", "&lt;");
		raw = raw.replaceAll(">", "&gt;");
		return raw;
	}
	
}
