package cc.sukazyo.cono.morny.util.tgapi.formatting;

import javax.annotation.Nonnull;

public class MsgEscape {
	
	@Nonnull
	public static String escapeHtml (@Nonnull String raw) {
		raw = raw.replaceAll("&", "&amp;");
		raw = raw.replaceAll("<", "&lt;");
		raw = raw.replaceAll(">", "&gt;");
		return raw;
	}
	
}
