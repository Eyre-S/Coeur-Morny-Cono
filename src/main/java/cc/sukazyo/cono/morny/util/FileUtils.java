package cc.sukazyo.cono.morny.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
	
	public static String getMD5Three (String path) {
		BigInteger bi;
		try {
			byte[] buffer = new byte[8192];
			int len;
			MessageDigest md = MessageDigest.getInstance("MD5");
			File f = new File(path);
			FileInputStream fis = new FileInputStream(f);
			while ((len = fis.read(buffer)) != -1) {
				md.update(buffer, 0, len);
			}
			fis.close();
			byte[] b = md.digest();
			bi = new BigInteger(1, b);
		} catch (NoSuchAlgorithmException | IOException e) {
			e.printStackTrace(System.out);
			return e.getMessage();
		}
		return bi.toString(16);
	}
	
}
