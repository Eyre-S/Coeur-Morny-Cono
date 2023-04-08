package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class UniversalCommand {
	
	@Nonnull
	public static String[] format (@Nonnull String com) {
		
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
					if (i >= coma.length) {
						break;
					} else if (coma[i] == '"') {
						break;
					} else if (coma[i] == '\\' && i+1 < coma.length && (coma[i+1] == '"' || coma[i+1] == '\\')) {
						i++;
						tmp.append(coma[i]);
					} else {
						tmp.append(coma[i]);
					}
				}
			} else if (coma[i] == '\\' && i+1 < coma.length && (coma[i+1] == ' ' || coma[i+1] == '"' || coma[i+1] == '\\')) {
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
	
}
