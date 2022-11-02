package cc.sukazyo.cono.morny.util.tgapi.formatting;

import cc.sukazyo.cono.morny.util.CommonConvert;
import cc.sukazyo.cono.morny.util.CommonEncrypt;

import javax.annotation.Nonnull;

public class NamedUtils {
	
	public static String inlineIds (@Nonnull String tag) {
		return inlineIds(tag, "");
	}
	
	public static String inlineIds (@Nonnull String tag, @Nonnull String taggedData) {
		return CommonConvert.byteArrayToHex(CommonEncrypt.hashMd5(tag+taggedData));
	}

}
