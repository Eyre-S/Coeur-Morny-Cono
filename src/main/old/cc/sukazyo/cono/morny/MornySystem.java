package cc.sukazyo.cono.morny;

import cc.sukazyo.cono.morny.daemon.MornyReport;
import cc.sukazyo.cono.morny.internal.BuildConfigField;
import cc.sukazyo.cono.morny.util.FileUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

/**
 * Morny Cono 的 Coeur 的程序属性存放类
 */
public class MornySystem {
	
	/**
	 * 程序的语义化版本号.
	 * <p>
	 * 这个版本号包含了以下的 {@link #VERSION_BASE}, {@link #VERSION_DELTA} 字段，
	 * 但不包含作为附加属性的构建时的{@link BuildConfig#COMMIT git 状态}属性
	 * <p>
	 * 这个格式的版本号也是在 maven 包仓库中所使用的版本号
	 * @since 1.0.0-alpha4
	 */
	@BuildConfigField @Nonnull public static final String VERSION = BuildConfig.VERSION;
	/**
	 * 程序的完整语义化版本号.
	 * <p>
	 * 包含了全部的 {@link #VERSION_BASE}, {@link #VERSION_DELTA}, 以及{@link BuildConfig#COMMIT git 状态}属性。
	 * <small>虽然仍旧不包含{@link #CODENAME}属性</small>
	 * <p>
	 * 这个格式的版本号也是 gradle 构建配置使用的版本号，也在普通打包时生成文件时使用
	 * @since 1.0.0-alpha4.2
	 */
	@BuildConfigField @Nonnull public static final String VERSION_FULL = BuildConfig.VERSION_FULL;
	/**
	 * 程序的基础版本号.
	 * <p>
	 * 它只包含了版本号中的主要信息：例如 {@code 0.8.0.5}, {@code 1.0.0-alpha-3},
	 * 而不会有用于精确定义的 {@link #VERSION_DELTA} 字段和作为附加使用的 {@link BuildConfig#COMMIT git commit 信息}
	 * @since 1.0.0-alpha4
	 */
	@BuildConfigField @Nonnull public static final String VERSION_BASE = BuildConfig.VERSION_BASE;
	/**
	 * 程序的版本 delta.
	 * —— 设计上用于在一个基版本当中分出不同构建的版本.
	 * <p>
	 * {@link null} 作为值，表示这个字段没有被使用.
	 * <p>
	 * 版本 delta 会以 {@code -δversion-delta} 的形式附着在 {@link #VERSION_BASE} 之后.
	 * 两者合并后的版本号格式即为 {@link #VERSION}
	 * <p>
	 * 在发行版本中一般不应该被使用.
	 * <p>
	 * <small>目前并不多被使用.</small>
	 * @since 1.0.0-alpha4
	 */
	@BuildConfigField @Nullable public static final String VERSION_DELTA = BuildConfig.VERSION_DELTA;
	
	/**
	 * Morny Coeur 当前的版本代号.
	 * <p>
	 * 一个单个单词，一般作为一个大版本的名称，只在重大更新改变<br>
	 * 格式保持为仅由小写字母和数字组成<br>
	 * 有时也可能是复合词或特殊的词句<br>
	 * <br>
	 */
	@BuildConfigField @Nonnull public static final String CODENAME = BuildConfig.CODENAME;
	
	/**
	 * Coeur 的代码仓库的链接. 它应该链接到当前程序的源码主页.
	 * <p>
	 * {@link null} 表示这个属性在构建时未被设置（或没有源码主页）
	 * @since 1.0.0-alpha4
	 */
	@BuildConfigField @Nullable public static final String CODE_STORE = BuildConfig.CODE_STORE;
	/**
	 * Coeur 的 git commit 链接.
	 * <p>
	 * 它应该是一个可以通过 {@link String#format(String, Object...)} 要求格式的链接模板，带有一个 {@link String} 类型的槽位 ——
	 * 通过 <code>String.format(COMMIT_PATH, {@link BuildConfig#COMMIT})</code> 即可取得当前当前程序所基于的 commit 的链接。
	 * @since 1.0.0-alpha4
	 */
	@BuildConfigField @Nullable public static final String COMMIT_PATH = BuildConfig.COMMIT_PATH;
	
	/** @see #VERSION_DELTA */
	@BuildConfigField
	public static boolean isUseDelta () { return VERSION_DELTA != null; }
	
	/** @see BuildConfig#COMMIT */
	@BuildConfigField
	@SuppressWarnings("ConstantConditions")
	public static boolean isGitBuild () { return BuildConfig.COMMIT != null; }
	
	/** @see BuildConfig#COMMIT */
	@BuildConfigField
	public static boolean isCleanBuild () { return BuildConfig.CLEAN_BUILD; }
	
	/**
	 * 获取程序的当前构建所基于的 git commit 的链接.
	 * <p>
	 * 如果 {@link #COMMIT_PATH}<small>（一般表示没有公开储存库）</small>
	 * 或是 {@link BuildConfig#COMMIT}<small>（一般表示程序的构建环境没有使用 git）</small>
	 * 任何一个不可用，则此方法也不可用。
	 *
	 * @return 当前构建的 git commit 链接，为空则表示不可用。
	 * @see #COMMIT_PATH
	 * @since 1.0.0-alpha4
	 */
	@Nullable
	@BuildConfigField
	@SuppressWarnings("ConstantConditions")
	public static String currentCodePath () {
		if (COMMIT_PATH == null || !isGitBuild()) return null;
		return String.format(COMMIT_PATH, BuildConfig.COMMIT);
	}
	
	
	
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
			return "<non-jar-runtime>";
		} catch (NoSuchAlgorithmException e) {
			Log.logger.error(Log.exceptionLog(e));
			MornyReport.exception(e, "<coeur-md5/calculation-error>");
			return "<calculation-error>";
		}
	}
	
}
