package cc.sukazyo.cono.morny;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.*;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

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
	
	public final boolean eventIgnoreOutdated;
	/**
	 * morny 的事件忽略前缀时间<br>
	 * <br>
	 * {@link cc.sukazyo.cono.morny.bot.event.MornyOnUpdateTimestampOffsetLock}
	 * 会根据这里定义的时间戳取消掉比此时间更早的事件链
	 */
	public final long eventOutdatedTimestamp;
	
	/* ======================================= *
	 *  system: command list automation        *
	 * ======================================= */
	
	public final boolean commandLoginRefresh;
	public final boolean commandLogoutClear;
	
	/* ======================================= *
	 *  system: morny report                   *
	 * ======================================= */
	
	/**
	 * 控制 Morny Coeur 系统的报告的报告对象.
	 * @since 1.0.0-alpha5
	 */
	public final long reportToChat;
	
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
		if (prototype.eventOutdatedTimestamp < 1) throw new CheckFailure.UnsetEventOutdatedTimestamp();
		this.eventOutdatedTimestamp = prototype.eventOutdatedTimestamp;
		this.commandLoginRefresh = prototype.commandLoginRefresh;
		this.commandLogoutClear = prototype.commandLogoutClear;
		this.dinnerTrustedReaders = prototype.dinnerTrustedReaders;
		this.dinnerChatId = prototype.dinnerChatId;
		this.reportToChat = prototype.reportToChat;
		this.medicationNotifyToChat = prototype.medicationNotifyToChat;
		this.medicationTimerUseTimezone = prototype.medicationTimerUseTimezone;
		prototype.medicationNotifyAt.forEach(i -> { if (i < 0 || i > 23) throw new CheckFailure.UnavailableTimeInMedicationNotifyAt(); });
		this.medicationNotifyAt = prototype.medicationNotifyAt;
	}
	
	public static class CheckFailure extends RuntimeException {
		public static class NullTelegramBotKey extends CheckFailure {}
		public static class UnsetEventOutdatedTimestamp extends CheckFailure {}
		public static class UnavailableTimeInMedicationNotifyAt extends CheckFailure {}
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
		public long eventOutdatedTimestamp = -1;
		public boolean commandLoginRefresh = false;
		public boolean commandLogoutClear = false;
		@Nonnull public final Set<Long> dinnerTrustedReaders = new HashSet<>();
		public long dinnerChatId = -1L;
		public long reportToChat = -1L;
		public long medicationNotifyToChat = -1L;
		@Nonnull public ZoneOffset medicationTimerUseTimezone = ZoneOffset.UTC;
		@Nonnull public final Set<Integer> medicationNotifyAt = new HashSet<>();
		
	}
	
}
