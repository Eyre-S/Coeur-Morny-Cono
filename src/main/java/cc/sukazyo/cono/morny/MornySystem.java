package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.util.FileUtils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

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
	 * Morny Coeur 当前的版本代号.<br>
	 * 一个单个单词，一般作为一个大版本的名称，只在重大更新改变<br>
	 * 格式保持为仅由小写字母和数字组成<br>
	 * 有时也可能是复合词或特殊的词句<br>
	 * <br>
	 * 会由 gradle 任务 {@code updateVersionCode} 更新
	 */
	public static final String CODENAME = GradleProjectConfigures.CODENAME;
	
	/**
	 * 获取程序 jar 文件的 md5-hash 值<br>
	 * <br>
	 * 只支持 jar 文件方式启动的程序 ——
	 * 如果是通过 classpath 来启动，程序无法找到本体jar文件，则会返回 {@code <non-jar-runtime>} 文本
	 * <br>
	 * 值格式为 {@link java.lang.String}
	 *
	 * @return 程序jar文件的 md5-hash 值字符串，或 {@code <non-jar-runtime>} 如果出现错误
	 */
	@Nonnull
	public static String getJarMd5() {
		try {
			return FileUtils.getMD5Three(MornyCoeur.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace(System.out);
			return "<non-jar-runtime>";
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace(System.out);
			return "<calculation-error>";
		}
	}
	
}
