package cc.sukazyo.cono.morny.util;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
	
	@Nonnull
	public static String getMD5Three (@Nonnull String path) throws IOException, NoSuchAlgorithmException {
		final BigInteger bi;
		final byte[] buffer = new byte[8192];
		int len;
		final MessageDigest md = MessageDigest.getInstance("MD5");
		final FileInputStream fis = new FileInputStream(path);
		while ((len = fis.read(buffer)) != -1) {
			md.update(buffer, 0, len);
		}
		fis.close();
		final byte[] b = md.digest();
		bi = new BigInteger(1, b);
		return bi.toString(16);
	}
	
}
