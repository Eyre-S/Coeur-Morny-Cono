package cc.sukazyo.cono.morny.core;

import cc.sukazyo.cono.morny.core.bot.event.MornyOnUpdateTimestampOffsetLock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.*;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

public class MornyConfig {
	
	/**
	 * 表示一个字段的值属于敏感数据，不应该被执行打印等操作。
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@Target({ElementType.FIELD, ElementType.METHOD})
	public @interface Sensitive {}
	
	/* ======================================= *
	 *  Config props Names Definition          *
	 * ======================================= */
	
	public static final String PROP_TOKEN_KEY_DEFAULT = "TELEGRAM_BOT_API_TOKEN";
	public static final String PROP_TOKEN_MORNY_KEY = "MORNY_TG_TOKEN";
	public static final String[] PROP_TOKEN_KEY = {PROP_TOKEN_KEY_DEFAULT, PROP_TOKEN_MORNY_KEY};
	
	/* ======================================= *
	 *  telegram bot login config              *
	 * ======================================= */
	
	/**
	 * Morny Telegram 使用的 API 服务器.
	 * <p>
	 * 不设定的话，默认将会使用 {@code https://api.telegram.org/bot}
	 */
	@Nullable public final String telegramBotApiServer;
	/**
	 * Morny Telegram 使用的 API 服务器的 file 服务路径.
	 * <p>
	 * 不设定的话，默认将会使用 {@value com.pengrad.telegrambot.impl.FileApi#FILE_API}
	 */
	@Nullable public final String telegramBotApiServer4File;
	
	/**
	 * morny 使用的 telegram bot 的 bot api token.
	 * <p>
	 * 这个值必须设定。
	 */
	@Nonnull @Sensitive public final String telegramBotKey;
	/**
	 * morny 所使用的 bot 的 username.
	 * <p>
	 * 如果设定了这个值，则在 morny 登录 bot 时将会检查所登录的 bot 的 username 是否和这里设定的 username 匹配。
	 * 如果不匹配，则会拒绝登录然后报错。
	 * <p>
	 * 如果没有设定这个值，则不会对登录 bot 的 username 进行限制。
	 */
	@Nullable public final String telegramBotUsername;
	
	/* ======================================= *
	 *  morny trusted config                   *
	 * ======================================= */
	
	/**
	 * morny 的主人.
	 * <p>
	 * 这项值的对象总是会被{@link MornyTrusted 信任管理器}认为是可信任的
	 */
	public final long trustedMaster;
	/**
	 * morny 可信群聊的 id.
	 * <p>
	 * {@link MornyTrusted 信任管理器}将会认为这个群聊中的所有拥有
	 * {@link com.pengrad.telegrambot.model.ChatMember.Status#administrator administrator} 权限的成员是可信任的。
	 * <p>
	 * id 需要符合 bot api 标准。
	 */
	public final long trustedChat;
	
	/* ======================================= *
	 *  system: event ignore                   *
	 * ======================================= */
	
	/**
	 * Morny 是否忽略过期事件.
	 * <br>
	 * 过期事件即发生时间比 {@link MornyCoeur#coeurStartTimestamp()} 早的事件。
	 * <br>
	 * 如果此项设置为 true, 则 {@link MornyOnUpdateTimestampOffsetLock}
	 * 会使事件时间比 {@link MornyCoeur#coeurStartTimestamp()} 早的事件跳过处理
	 */
	public final boolean eventIgnoreOutdated;
	
	/* ======================================= *
	 *  system: command list automation        *
	 * ======================================= */
	
	public final boolean commandLoginRefresh;
	public final boolean commandLogoutClear;
	
	/* ======================================= *
	 *  system: http server                    *
	 * ======================================= */
	
	public final int httpPort;
	
	/* ======================================= *
	 *  function: reporter                     *
	 * ======================================= */
	
	/**
	 * 控制 Morny Coeur 系统的报告的报告对象.
	 * @since 1.0.0-alpha5
	 */
	public final long reportToChat;
	
	/**
	 * 控制 Morny Coeur 系统的报告的基准时间.
	 * <p> 
	 * 仅会用于 {@link cc.sukazyo.cono.morny.reporter.MornyReport} 内的时间敏感的报告，
	 * 不会用于 {@code /info} 命令等位置。
	 * <p>
	 * 默认使用 {@link TimeZone#getDefault()}.
	 * 
	 * @since 1.3.0
	 */
	@Nonnull public final TimeZone reportZone;
	
	/* ======================================= *
	 *  function: dinner query tool            *
	 * ======================================= */
	
	@Nonnull public final Set<Long> dinnerTrustedReaders;
	public final long dinnerChatId;
	
	/* ======================================= *
	 *  function: medication timer             *
	 * ======================================= */
	
	public final long medicationNotifyToChat;
	
	@Nonnull public final ZoneOffset medicationTimerUseTimezone;
	
	@Nonnull public final Set<Integer> medicationNotifyAt;
	
	/* ======================================= *
	 *  End Configs | ConfigBuilder            *
	 * ======================================= */
	
	private MornyConfig (@Nonnull Prototype prototype) throws CheckFailure {
		this.telegramBotApiServer = prototype.telegramBotApiServer;
		this.telegramBotApiServer4File = prototype.telegramBotApiServer4File;
		if (prototype.telegramBotKey == null) throw new CheckFailure.NullTelegramBotKey();
		this.telegramBotKey = prototype.telegramBotKey;
		this.telegramBotUsername = prototype.telegramBotUsername;
		this.trustedMaster = prototype.trustedMaster;
		this.trustedChat = prototype.trustedChat;
		this.eventIgnoreOutdated = prototype.eventIgnoreOutdated;
		this.commandLoginRefresh = prototype.commandLoginRefresh;
		this.commandLogoutClear = prototype.commandLogoutClear;
		this.dinnerTrustedReaders = prototype.dinnerTrustedReaders;
		this.dinnerChatId = prototype.dinnerChatId;
		this.reportToChat = prototype.reportToChat;
		this.reportZone = prototype.reportZone;
		this.medicationNotifyToChat = prototype.medicationNotifyToChat;
		this.medicationTimerUseTimezone = prototype.medicationTimerUseTimezone;
		prototype.medicationNotifyAt.forEach(i -> { if (i < 0 || i > 23) throw new CheckFailure.UnavailableTimeInMedicationNotifyAt(); });
		this.medicationNotifyAt = prototype.medicationNotifyAt;
		if (prototype.httpPort < 0 || prototype.httpPort > 65535) throw new CheckFailure.UnavailableHttpPort();
		this.httpPort = prototype.httpPort;
	}
	
	public static class CheckFailure extends RuntimeException {
		public static class NullTelegramBotKey extends CheckFailure {}
		public static class UnavailableTimeInMedicationNotifyAt extends CheckFailure {}
		public static class UnavailableHttpPort extends CheckFailure {}
	}
	
	public static class Prototype {
		
		public MornyConfig build () {
			return new MornyConfig(this);
		}
		
		@Nullable public String telegramBotApiServer = null;
		@Nullable public String telegramBotApiServer4File = null;
		@Nullable public String telegramBotKey = null;
		@Nullable public String telegramBotUsername = null;
		public long trustedMaster = -1L;
		public long trustedChat = -1L;
		public boolean eventIgnoreOutdated = false;
		public boolean commandLoginRefresh = false;
		public boolean commandLogoutClear = false;
		@Nonnull public final Set<Long> dinnerTrustedReaders = new HashSet<>();
		public long dinnerChatId = -1L;
		public long reportToChat = -1L;
		@Nonnull public TimeZone reportZone = TimeZone.getDefault();
		public long medicationNotifyToChat = -1L;
		@Nonnull public ZoneOffset medicationTimerUseTimezone = ZoneOffset.UTC;
		@Nonnull public final Set<Integer> medicationNotifyAt = new HashSet<>();
		public int httpPort = 30179;
		
	}
	
}
