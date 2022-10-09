package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.GradleProjectConfigures;
import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornySystem;
import cc.sukazyo.cono.morny.data.MornyJrrp;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import cc.sukazyo.cono.morny.util.tgapi.formatting.TGToString;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.DeleteMyCommands;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cc.sukazyo.cono.morny.Log.logger;
import static cc.sukazyo.cono.morny.util.CommonFormat.formatDate;
import static cc.sukazyo.cono.morny.util.CommonFormat.formatDuration;
import static cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml;

public class MornyCommands {
	
	private final Map<String, ISimpleCommand> commands = new LinkedHashMap<>();
	
	private void pushCommandTo (@Nonnull String name, @Nonnull ISimpleCommand instance) {
		if (commands.containsKey(name)) {
			logger.warn(String.format("""
					Telegram command instance named "%s" already exists and will be override by another command instance
					- current: %s
					- new : %s""",
					name,
					commands.get(name).getClass().getName(),
					instance.getClass().getName()
			));
		}
		commands.put(name, instance);
	}
	
	public void register (@Nonnull ISimpleCommand... list) {
		for (ISimpleCommand instance : list) {
			final String[] aliases = instance.getAliases();
			pushCommandTo(instance.getName(), instance);
			if (aliases!=null) for (String alias : aliases) pushCommandTo(alias, instance);
		}
	}
	
	@SuppressWarnings("NonAsciiCharacters")
	public MornyCommands () {
		
		register(
				new ON(),
				new Hello(), new HelloOnStart(),
				new GetUsernameAndId(),
				new EventHack(),
				new Nbnhhsh(),
				new Ip186Query.Ip(),
				new Ip186Query.Whois(),
				new EncUtils(),
				new SaveData(),
				new Version(),
				new MornyRuntime(),
				new Jrrp(),
				new Exit(), new ExitAlias()
		);
		
		// 特殊的命令
		register(
				new DirectMsgClear()
		);
		
		// 统一注册这些奇怪的东西&.&
		register(
				new 喵呜.抱抱(),
				new 喵呜.揉揉(),
				new 喵呜.蹭蹭(),
				new 喵呜.贴贴(),
				new 私わね(),
				new 喵呜.Progynova()
		);
		
	}
	
	public boolean execute (@Nonnull InputCommand command, @Nonnull Update event) {
		if (commands.containsKey(command.getCommand())) {
			commands.get(command.getCommand()).execute(command, event);
			return true;
		}
		return nonCommandExecutable(event, command);
	}
	
	public void automaticUpdateList () {
		BotCommand[] commandList = getCommandListTelegram();
		automaticRemoveList();
		MornyCoeur.extra().exec(new SetMyCommands(
				commandList
		));
		logger.info("automatic updated telegram command list :\n" + commandListToString(commandList));
	}
	
	public void automaticRemoveList () {
		MornyCoeur.extra().exec(new DeleteMyCommands());
		logger.info("cleaned up command list.");
	}
	
	private String commandListToString (@Nonnull BotCommand[] list) {
		StringBuilder builder = new StringBuilder();
		for (BotCommand signal : list) {
			builder.append(signal.command()).append(" - ").append(signal.description()).append("\n");
		}
		return builder.substring(0, builder.length()-1);
	}
	
	public BotCommand[] getCommandListTelegram () {
		final List<BotCommand> telegramFormatListing = new ArrayList<>();
		commands.forEach((regKey, command) -> {
			if (command instanceof ITelegramCommand && regKey.equals(command.getName())) {
				telegramFormatListing.add(formatTelegramCommandListLine(
						command.getName(),
						((ITelegramCommand)command).getParamRule(),
						((ITelegramCommand)command).getDescription()
				));
				if (command.getAliases() != null) for (String alias : command.getAliases()) {
					telegramFormatListing.add(formatTelegramCommandListLine(alias, "", "↑"));
				}
			}
		});
		return telegramFormatListing.toArray(BotCommand[]::new);
	}
	
	private BotCommand formatTelegramCommandListLine (@Nonnull String commandName, @Nonnull String paramRule, @Nonnull String intro) {
		return new BotCommand(commandName, "".equals(paramRule) ? (intro) : (paramRule+" - "+intro));
	}
	
	private boolean nonCommandExecutable (Update event, InputCommand command) {
		if (command.getTarget() == null) return false; // 无法解析的命令，转交事件链后代处理
		else { // 无法解析的显式命令格式，报错找不到命令
			MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_404
					).replyToMessageId(event.message().messageId())
			);
			return true;
		}
	}
	
	/// /// /// /// /// /// /// /// ///
	///
	///   Old Simple Command Block
	///
	
	private static class ON implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "o"; }
		@Nullable
		@Override public String[] getAliases () { return null; }
		@Nonnull @Override public String getParamRule () { return ""; }
		@Nonnull @Override public String getDescription () { return "检查是否在线"; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) { onCommandOnExec(event); }
	}
	private static void onCommandOnExec (@Nonnull Update event) {
		MornyCoeur.extra().exec(new SendSticker(
						event.message().chat().id(),
						TelegramStickers.ID_ONLINE_STATUS_RETURN
				).replyToMessageId(event.message().messageId())
		);
	}
	
	private static class Hello implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "hello"; }
		@Nullable @Override public String[] getAliases () { return new String[]{"hi"}; }
		@Nonnull @Override public String getParamRule () { return ""; }
		@Nonnull @Override public String getDescription () { return "打招呼"; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) { onCommandHelloExec(event); }
	}
	private static class HelloOnStart implements ISimpleCommand { @Nonnull @Override public String getName () { return "start"; }@Nullable @Override public String[] getAliases () { return new String[0]; }@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) { onCommandHelloExec(event); }}
	private static void onCommandHelloExec (@Nonnull Update event) {
		MornyCoeur.extra().exec(new SendSticker(
						event.message().chat().id(),
						TelegramStickers.ID_HELLO
				).replyToMessageId(event.message().messageId())
		);
	}
	
	private static class Exit implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "exit"; }
		@Nullable @Override public String[] getAliases () { return new String[0]; }
		@Nonnull @Override public String getParamRule () { return ""; }
		@Nonnull @Override public String getDescription () { return "关闭 Bot （仅可信成员）"; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) { onCommandExitExec(event); }
	}
	private static class ExitAlias implements ISimpleCommand {
		@Nonnull @Override public String getName () { return "quit"; }
		@Nullable @Override public String[] getAliases () { return new String[]{"stop"}; }
		@Override public void execute (@NotNull InputCommand command, @NotNull Update event) { onCommandExitExec(event); }
	}
	private static void onCommandExitExec (@Nonnull Update event) {
		if (MornyCoeur.trustedInstance().isTrusted(event.message().from().id())) {
			MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_EXIT
					).replyToMessageId(event.message().messageId())
			);
			logger.info("Morny exited by user " + TGToString.as(event.message().from()).toStringLogTag());
			System.exit(0);
		} else {
			MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_403
					).replyToMessageId(event.message().messageId())
			);
			logger.info("403 exited tag from user " + TGToString.as(event.message().from()).toStringLogTag());
		}
	}
	
	private static class Version implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "version"; }
		@Nullable @Override public String[] getAliases () { return null; }
		@Nonnull @Override public String getParamRule () { return ""; }
		@Nonnull @Override public String getDescription () { return "检查 Bot 版本信息"; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) { onCommandVersionExec(event); }
	}
	private static void onCommandVersionExec (@Nonnull Update event) {
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				String.format(
						"""
						version:
						- Morny <code>%s</code>
						- <code>%s</code>
						core md5_hash:
						- <code>%s</code>
						compile timestamp:
						- <code>%d</code>
						- <code>%s [UTC]</code>""",
						escapeHtml(MornySystem.CODENAME.toUpperCase()),
						escapeHtml(MornySystem.VERSION),
						escapeHtml(MornySystem.getJarMd5()),
						GradleProjectConfigures.COMPILE_TIMESTAMP,
						escapeHtml(formatDate(GradleProjectConfigures.COMPILE_TIMESTAMP, 0))
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
	private static class MornyRuntime implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "runtime"; }
		@Nullable @Override public String[] getAliases () { return null; }
		@Nonnull @Override public String getParamRule () { return ""; }
		@Nonnull @Override public String getDescription () { return "获取 Bot 运行时信息（包括版本号）"; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) { onCommandRuntimeExec(event); }
	}
	/**
	 * @since 0.4.1.2
	 */
	private static void onCommandRuntimeExec (@Nonnull Update event) {
		String hostname;
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			hostname = "<unknown>";
		}
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				String.format("""
								system:
								- <code>%s</code>
								- <code>%s</code>
								- <code>%s</code>
								java runtime:
								- <code>%s</code>
								- <code>%s</code>
								vm memory:
								- <code>%d</code> / <code>%d</code> MB
								- <code>%d</code> cores
								coeur version:
								- <code>%s</code> (<code>%s</code>)
								- <code>%s</code>
								- <code>%s [UTC]</code>
								- [<code>%d</code>]
								continuous:
								- <code>%s</code>
								- [<code>%d</code>]
								- <code>%s [UTC]</code>
								- [<code>%d</code>]""",
						// system
						escapeHtml(hostname),
						escapeHtml(String.format("%s (%s)", System.getProperty("os.name"), System.getProperty("os.arch"))),
						escapeHtml(System.getProperty("os.version")),
						// java
						escapeHtml(System.getProperty("java.vm.vendor")+"."+System.getProperty("java.vm.name")),
						escapeHtml(System.getProperty("java.vm.version")),
						// memory
						Runtime.getRuntime().totalMemory() / 1024 / 1024,
						Runtime.getRuntime().maxMemory() / 1024 / 1024,
						Runtime.getRuntime().availableProcessors(),
						// version
						escapeHtml(MornySystem.VERSION),
						escapeHtml(MornySystem.CODENAME),
						escapeHtml(MornySystem.getJarMd5()),
						escapeHtml(formatDate(GradleProjectConfigures.COMPILE_TIMESTAMP, 0)),
						GradleProjectConfigures.COMPILE_TIMESTAMP,
						// continuous
						escapeHtml(formatDuration(System.currentTimeMillis() - MornyCoeur.coeurStartTimestamp)),
						System.currentTimeMillis() - MornyCoeur.coeurStartTimestamp,
						escapeHtml(formatDate(MornyCoeur.coeurStartTimestamp, 0)),
						MornyCoeur.coeurStartTimestamp
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
	private static class Jrrp implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "jrrp"; }
		@Nullable @Override public String[] getAliases () { return null; }
		@Nonnull @Override public String getParamRule () { return ""; }
		@Nonnull @Override public String getDescription () { return "获取 (假的) jrrp"; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) { onCommandJrrpExec(event); }
	}
	private static void onCommandJrrpExec (Update event) {
		final double jrrp = MornyJrrp.getJrrpFromTelegramUser(event.message().from(), System.currentTimeMillis());
		final String endChar = jrrp>70 ? "!" : jrrp>30 ? ";" : "...";
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				String.format(
						"%s 在(utc的)今天的运气指数是———— <code>%.2f%%</code> %s",
						TGToString.as(event.message().from()).fullnameRefHtml(),
						jrrp, escapeHtml(endChar)
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
	private static class SaveData implements ITelegramCommand {
		@Nonnull @Override public String getName () { return "save"; }
		@Nullable @Override public String[] getAliases () { return null; }
		@Nonnull @Override public String getParamRule () { return ""; }
		@Nonnull @Override public String getDescription () { return "保存缓存数据到文件（仅可信成员）"; }
		@Override public void execute (@Nonnull InputCommand command, @Nonnull Update event) { onSaveDataExec(event); }
	}
	/**
	 * @since 0.4.3.0
	 */
	private static void onSaveDataExec (Update event) {
		if (MornyCoeur.trustedInstance().isTrusted(event.message().from().id())) {
			logger.info("called save from command by " + TGToString.as(event.message().from()).toStringLogTag());
			MornyCoeur.callSaveData();
			MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_SAVED
					).replyToMessageId(event.message().messageId())
			);
		} else {
			MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_403
					).replyToMessageId(event.message().messageId())
			);
			logger.info("403 call save tag from user " + TGToString.as(event.message().from()).toStringLogTag());
		}
	}

}
