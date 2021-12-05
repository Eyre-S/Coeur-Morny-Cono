package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.FileUtils;

import javax.annotation.Nonnull;
import java.net.URISyntaxException;

/**
 * Morny Cono 的 Coeur 的程序属性存放类
 */
public class MornySystem {
	
	/**
	 * 程序的语义化版本号<br>
	 * 会由 gradle 任务 {@code updateVersionCode} 更新
	 */
	public static final String VERSION = GradleProjectConfigures.VERSION;
	
	/**
	 * 获取程序 jar 文件的 md5-hash 值<br>
	 * <br>
	 * 只支持 jar 文件方式启动的程序 ——
	 * 如果是通过 classpath 来启动，则会返回找不到文件的错误数据<br>
	 * - 或许需要注意，这种情况下会出现程序文件所在的路径<br>
	 * <br>
	 * 值格式为 {@link java.lang.String}
	 *
	 * @return 程序jar文件的 md5-hash 值字符串，或错误信息
	 */
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
