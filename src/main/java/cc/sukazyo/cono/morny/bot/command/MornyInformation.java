package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.BuildConfig;
import cc.sukazyo.cono.morny.MornyAbout;
import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornySystem;
import cc.sukazyo.cono.morny.data.TelegramImages;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.tgapi.ExtraAction;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import static cc.sukazyo.cono.morny.util.CommonFormat.formatDate;
import static cc.sukazyo.cono.morny.util.CommonFormat.formatDuration;
import static cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml;

public class MornyInformation implements ITelegramCommand {
	
	private static final String SUB_STICKER = "stickers";
	private static final String SUB_RUNTIME = "runtime";
	private static final String SUB_VERSION = "version";
	private static final String SUB_VERSION_2 = "v";
	
	@Nonnull @Override public String getName () { return "info"; }
	@Nullable @Override public String[] getAliases () { return new String[0]; }
	@Nonnull @Override public String getParamRule () { return "[(version|runtime|stickers[.IDs])]"; }
	@Nonnull @Override public String getDescription () { return "输出当前 Morny 的各种信息"; }
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		if (!command.hasArgs()) {
			echoInfo(event.message().chat().id(), event.message().messageId());
			return;
		}
		
		final String action = command.getArgs()[0];
		
		if (action.startsWith(SUB_STICKER)) {
			echoStickers(command, event);
		} else if (action.equals(SUB_RUNTIME)) {
			echoRuntime(event);
		} else if (action.equals(SUB_VERSION) || action.equals(SUB_VERSION_2)) {
			echoVersion(event);
		} else {
			echo404(event);
		}
		
	}
	
	/**
	 * Subcommand <u>/info</u> without params.
	 *
	 * @since 1.0.0-RC4
	 */
	public void echoInfo (long chatId, int replayToMessage) {
		MornyCoeur.extra().exec(new SendPhoto(
				chatId,
				getAboutPic()
		).caption("""
				<b>Morny Cono</b>
				来自安妮的侍从小鼠。
				————————————————
				%s""".formatted(getMornyAboutLinksHTML())
		).parseMode(ParseMode.HTML).replyToMessageId(replayToMessage));
	}
	
	/**
	 * subcommand <u>/info stickers</u>
	 *
	 * @see #SUB_STICKER
	 */
	public void echoStickers (@Nonnull InputCommand command, @Nonnull Update event) {
		final long echoTo = event.message().chat().id();
		final int replyToMessage = event.message().messageId();
		String id = null;
		if (command.getArgs()[0].equals(SUB_STICKER)) {
			if (command.getArgs().length == 1) {
				id = "";
			} else if (command.getArgs().length == 2) {
				id = command.getArgs()[1];
			}
		} else if (command.getArgs().length == 1) {
			if (command.getArgs()[0].startsWith(SUB_STICKER+".") || command.getArgs()[0].startsWith(SUB_STICKER+"#")) {
				id = command.getArgs()[0].substring(SUB_STICKER.length()+1);
			}
		}
		if (id == null) { echo404(event); return; }
		echoStickers(id, echoTo, replyToMessage);
	}
	
	/**
	 * 向 telegram 输出一个或全部 sticker.
	 *
	 * @param id
	 *        sticker 在 {@link TelegramStickers} 中的字段名。
	 *        使用 {@link ""}(空字符串)(不是{@link null}) 表示输出全部 sticker
	 * @param chatId 目标 chat id
	 * @param messageId 要回复的消息 id，依据 {@link TelegramStickers#echoStickerByID(String, ExtraAction, long, int) 上游}
	 *                  逻辑，使用 {@link -1} 表示不回复消息。
	 *
	 * @see TelegramStickers#echoStickerByID(String, ExtraAction, long, int)
	 * @see TelegramStickers#echoAllStickers(ExtraAction, long, int)
	 */
	public static void echoStickers (@Nonnull String id, long chatId, int messageId) {
		if (id.isEmpty()) TelegramStickers.echoAllStickers(MornyCoeur.extra(), chatId, messageId);
		else TelegramStickers.echoStickerByID(id, MornyCoeur.extra(), chatId, messageId);
	}
	
	/**
	 * Subcommand <u>/info runtime</u>.
	 *
	 * @see #SUB_RUNTIME
	 *
	 * @since 1.0.0-alpha4
	 */
	public static void echoRuntime (@Nonnull Update event) {
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				String.format("""
								system:
								- <code>%s</code>
								- <code>%s</code> (<code>%s</code>) <code>%s</code>
								java runtime:
								- <code>%s</code>
								- <code>%s</code>
								vm memory:
								- <code>%d</code> / <code>%d</code> MB
								- <code>%d</code> cores
								coeur version:
								- %s
								- <code>%s</code>
								- <code>%s [UTC]</code>
								- [<code>%d</code>]
								continuous:
								- <code>%s</code>
								- [<code>%d</code>]
								- <code>%s [UTC]</code>
								- [<code>%d</code>]""",
						// system
						escapeHtml(getRuntimeHostName()==null ? "<unknown-host>" : getRuntimeHostName()),
						escapeHtml(System.getProperty("os.name")),
						escapeHtml(System.getProperty("os.arch")),
						escapeHtml(System.getProperty("os.version")),
						// java
						escapeHtml(System.getProperty("java.vm.vendor")+"."+System.getProperty("java.vm.name")),
						escapeHtml(System.getProperty("java.vm.version")),
						// memory
						Runtime.getRuntime().totalMemory() / 1024 / 1024,
						Runtime.getRuntime().maxMemory() / 1024 / 1024,
						Runtime.getRuntime().availableProcessors(),
						// version
						getVersionAllFullTagHtml(),
						escapeHtml(MornySystem.getJarMd5()),
						escapeHtml(formatDate(BuildConfig.CODE_TIMESTAMP, 0)),
						BuildConfig.CODE_TIMESTAMP,
						// continuous
						escapeHtml(formatDuration(System.currentTimeMillis() - MornyCoeur.coeurStartTimestamp)),
						System.currentTimeMillis() - MornyCoeur.coeurStartTimestamp,
						escapeHtml(formatDate(MornyCoeur.coeurStartTimestamp, 0)),
						MornyCoeur.coeurStartTimestamp
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
	/**
	 * Subcommand <u>/info version</u> or <u>/info v</u>.
	 *
	 * @see #SUB_VERSION
	 * @see #SUB_VERSION_2
	 *
	 * @since 1.0.0-alpha4
	 */
	public static void echoVersion (@Nonnull Update event) {
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				String.format(
						"""
						version:
						- Morny <code>%s</code>
						- <code>%s</code>%s%s
						core md5_hash:
						- <code>%s</code>
						coding timestamp:
						- <code>%d</code>
						- <code>%s [UTC]</code>""",
						escapeHtml(MornySystem.CODENAME.toUpperCase()),
						escapeHtml(MornySystem.VERSION_BASE),
						MornySystem.isUseDelta() ? String.format("-δ<code>%s</code>", escapeHtml(Objects.requireNonNull(MornySystem.VERSION_DELTA))) : "",
						MornySystem.isGitBuild() ? "\n- git "+getVersionGitTagHtml() : "",
						escapeHtml(MornySystem.getJarMd5()),
						BuildConfig.CODE_TIMESTAMP,
						escapeHtml(formatDate(BuildConfig.CODE_TIMESTAMP, 0))
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
	/**
	 * 取得 {@link MornySystem} 的 git commit 相关版本信息的 HTML 格式化标签.
	 *
	 * @return 格式类似于 <u>{@code 28e8c82a.δ}</u> 的以 HTML 方式格式化的版本号组件。
	 *         其中 {@code .δ} 对应着 {@link MornySystem#isCleanBuild}；
	 *         commit tag 字段如果支援 {@link MornySystem#currentCodePath} 则会以链接形式解析，否则则为 code 格式
	 *         <small>为了对 telegram api html 格式兼容所以不支援嵌套链接与code标签</small>。
	 *         如果 {@link MornySystem#isGitBuild} 为 {@link false}，则方法会返回 {@link ""}
	 *
	 * @since 1.0.0-beta2
	 */
	@Nonnull
	public static String getVersionGitTagHtml () {
		if (!MornySystem.isGitBuild()) return "";
		final StringBuilder g = new StringBuilder();
		final String cp = MornySystem.currentCodePath();
		if (cp == null) g.append(String.format("<code>%s</code>", BuildConfig.COMMIT.substring(0, 8)));
		else g.append(String.format("<a href='%s'>%s</a>", MornySystem.currentCodePath(), BuildConfig.COMMIT.substring(0, 8)));
		if (!MornySystem.isCleanBuild()) g.append(".<code>δ</code>");
		return g.toString();
	}
	
	/**
	 * 取得完整 Morny 版本的 HTML 格式化标签.
	 * <p>
	 * 相比于 {@link MornySystem#VERSION_FULL}，这个版本号还包含了 {@link MornySystem#CODENAME 版本 codename}。
	 * 各个部分也被以 HTML 的格式进行了格式化以可以更好的在富文本中插入使用.
	 * @return 基于 HTML 标签进行了格式化了的类似于
	 *         <u><code>{@link MornySystem#VERSION_BASE 5.38.2-alpha1}{@link MornySystem#isUseDelta() -δ}{@link MornySystem#VERSION_DELTA tt}{@link MornySystem#isGitBuild() +git.}{@link #getVersionGitTagHtml() 28e8c82a.δ}*{@link MornySystem#CODENAME TOKYO}</code></u>
	 *         的版本号。
	 * @since 1.0.0-beta2
	 */
	@Nonnull
	public static String getVersionAllFullTagHtml () {
		final StringBuilder v = new StringBuilder();
		v.append("<code>").append(MornySystem.VERSION_BASE).append("</code>");
		if (MornySystem.isUseDelta()) v.append("-δ<code>").append(MornySystem.VERSION_DELTA).append("</code>");
		if (MornySystem.isGitBuild()) v.append("+git.").append(getVersionGitTagHtml());
		v.append("*<code>").append(MornySystem.CODENAME.toUpperCase()).append("</code>");
		return v.toString();
	}
	
	/**
	 * 获取 coeur 运行时的宿主机的主机名
	 * @return coeur 宿主机主机名，或者 {@link null} 表示获取失败
	 */
	@Nullable
	public static String getRuntimeHostName () {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
	/**
	 * Get the about-pic (intro picture or featured image) of Morny.
	 *
	 * @return the Telegram file binary data of the about-pic.
	 * @throws IllegalStateException {@link TelegramImages.AssetsFileImage#get() get() image data} may
	 *                               throws {@link IllegalStateException} while read error.
	 */
	@Nonnull
	public static byte[] getAboutPic () {
		return TelegramImages.IMG_ABOUT.get();
	}
	
	private static void echo404 (@Nonnull Update event) {
		MornyCoeur.extra().exec(new SendSticker(
				event.message().chat().id(),
				TelegramStickers.ID_404
		).replyToMessageId(event.message().messageId()));
	}
	
	/**
	 * The formatted about links of Morny Cono and Morny Coeur.
	 * <p>
	 * With the Telegram HTML formatting, used in <u>/info</u> and <u>/start</u>.
	 * Provided the end user the links that can find resources about Morny.
	 */
	@Nonnull
	public static String getMornyAboutLinksHTML () {
		return """
				<a href='%s'>source code</a> | <a href='%s'>backup</a>
				<a href='%s'>反馈 / issue tracker</a>
				<a href='%s'>使用说明书 / user guide & docs</a>""".formatted(
				MornyAbout.MORNY_SOURCECODE_LINK, MornyAbout.MORNY_SOURCECODE_SELF_HOSTED_MIRROR_LINK,
				MornyAbout.MORNY_ISSUE_TRACKER_LINK,
				MornyAbout.MORNY_USER_GUIDE_LINK
		);
	}
	
}
