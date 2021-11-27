package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.FileUtils;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;

public class MornySystem {
	
	public static final String VERSION = "0.3.4";
	
	@Nonnull
	public static String getJarMd5() {
		try {
			return FileUtils.getMD5Three(MornyCoeur.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace(System.out);
			return e.getMessage();
		}
	}
	
}
