package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.FileUtils;

import java.net.URISyntaxException;

public class MornySystem {
	
	public static final String VERSION = "@G_DEV_VERSION@";
	
	public static String getJarMd5() {
		try {
			return FileUtils.getMD5Three(MornyCoeur.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace(System.out);
			return e.getMessage();
		}
	}
	
}
