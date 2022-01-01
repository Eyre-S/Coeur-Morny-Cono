package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.GradleProjectConfigures;
import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornySystem;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.bot.api.InputCommand;
import cc.sukazyo.cono.morny.bot.event.on_commands.EventHack;
import cc.sukazyo.cono.morny.bot.event.on_commands.GetUsernameAndId;
import cc.sukazyo.cono.morny.bot.event.on_commands.Ip186Query;
import cc.sukazyo.cono.morny.data.MornyJrrp;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;

import static cc.sukazyo.cono.morny.Log.logger;
import static cc.sukazyo.cono.morny.util.CommonFormatUtils.formatDate;
import static cc.sukazyo.cono.morny.util.CommonFormatUtils.formatDuration;
import static cc.sukazyo.cono.morny.util.StringUtils.escapeHtmlTelegram;

public class OnCommandExecute extends EventListener {
	
	@Override
	public boolean onMessage (@Nonnull Update event) {
		if (event.message().text() == null) {
			return false; // 检测到无消息文本，忽略掉命令处理
		}
		final InputCommand command = new InputCommand(event.message().text());
		if (command.getTarget() != null && !MornyCoeur.getUsername().equals(command.getTarget())) {
			return true; // 检测到命令并非针对 morny，退出整个事件处理链
		}
		switch (command.getCommand()) {
			case "/user":
				GetUsernameAndId.exec(command.getArgs(), event);
				break;
			case "/event_hack":
				EventHack.exec(event, command);
				break;
			case "/o":
				onCommandOnExec(event);
				break;
			case "/hi":
			case "/hello":
				onCommandHelloExec(event);
				break;
			case "/exit":
				onCommandExitExec(event);
				break;
			case "/version":
				onCommandVersionExec(event);
				break;
			case "/runtime":
				onCommandRuntimeExec(event);
				break;
			case "/save":
				onSaveDataExec(event);
				break;
			case "/jrrp":
				onCommandJrrpExec(event);
				break;
			case "/ip":
			case "/whois":
				Ip186Query.exec(event, command);
				break;
			default:
				return nonCommandExecutable(event, command);
		}
		return true; // 命令执行成功，标记事件为已处理，退出事件链
	}
	
	private boolean nonCommandExecutable (Update event, InputCommand command) {
		if (command.getTarget() == null) return false; // 无法解析的命令，转交事件链后代处理
		else { // 无法解析的显式命令格式，报错找不到命令
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_404
					).replyToMessageId(event.message().messageId())
			);
			return true;
		}
	}
	
	private void onCommandOnExec (@Nonnull Update event) {
		MornyCoeur.getAccount().execute(new SendSticker(
				event.message().chat().id(),
				TelegramStickers.ID_ONLINE_STATUS_RETURN
				).replyToMessageId(event.message().messageId())
		);
	}
	
	private void onCommandHelloExec (@Nonnull Update event) {
		MornyCoeur.getAccount().execute(new SendSticker(
						event.message().chat().id(),
						TelegramStickers.ID_HELLO
				).replyToMessageId(event.message().messageId())
		);
	}
	
	private void onCommandExitExec (@Nonnull Update event) {
		if (MornyCoeur.trustedInstance().isTrusted(event.message().from().id())) {
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_EXIT
					).replyToMessageId(event.message().messageId())
			);
			logger.info("Morny exited by user @" + event.message().from().username());
			System.exit(0);
		} else {
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_403
					).replyToMessageId(event.message().messageId())
			);
			logger.info("403 exited tag from user @" + event.message().from().username());
		}
	}
	
	private void onCommandVersionExec (@Nonnull Update event) {
		MornyCoeur.getAccount().execute(new SendMessage(
				event.message().chat().id(),
				String.format(
						"""
						version:
						- <code>%s</code>
						core md5_hash:
						- <code>%s</code>
						compile timestamp:
						- <code>%d</code>
						- <code>%s [UTC]</code>""",
						escapeHtmlTelegram(MornySystem.VERSION),
						escapeHtmlTelegram(MornySystem.getJarMd5()),
						GradleProjectConfigures.COMPILE_TIMESTAMP,
						escapeHtmlTelegram(formatDate(GradleProjectConfigures.COMPILE_TIMESTAMP, 0))
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
	/**
	 * @since 0.4.1.2
	 */
	private void onCommandRuntimeExec (@Nonnull Update event) {
		MornyCoeur.getAccount().execute(new SendMessage(
				event.message().chat().id(),
				String.format("""
								system:
								- <code>%s</code>
								- <code>%s</code>
								- <code>%d</code> cores
								java runtime:
								- <code>%s</code>
								- <code>%s</code>
								vm memory:
								- <code>%d</code> / <code>%d</code> MB
								morny version:
								- <code>%s</code>
								- <code>%s</code>
								- <code>%s [UTC]</code>
								- [<code>%d</code>]
								continuous:
								- <code>%s</code>
								- [<code>%d</code>]
								- <code>%s [UTC]</code>
								- [<code>%d</code>]""",
						// system
						escapeHtmlTelegram(System.getProperty("os.name")),
						escapeHtmlTelegram(System.getProperty("os.version")),
						Runtime.getRuntime().availableProcessors(),
						// java
						escapeHtmlTelegram(System.getProperty("java.vm.name")),
						escapeHtmlTelegram(System.getProperty("java.version")),
						// memory
						Runtime.getRuntime().totalMemory() / 1024 / 1024,
						Runtime.getRuntime().maxMemory() / 1024 / 1024,
						// version
						escapeHtmlTelegram(MornySystem.VERSION),
						escapeHtmlTelegram(MornySystem.getJarMd5()),
						escapeHtmlTelegram(formatDate(GradleProjectConfigures.COMPILE_TIMESTAMP, 0)),
						GradleProjectConfigures.COMPILE_TIMESTAMP,
						// continuous
						escapeHtmlTelegram(formatDuration(System.currentTimeMillis() - MornyCoeur.coeurStartTimestamp)),
						System.currentTimeMillis() - MornyCoeur.coeurStartTimestamp,
						escapeHtmlTelegram(formatDate(MornyCoeur.coeurStartTimestamp, 0)),
						MornyCoeur.coeurStartTimestamp
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
	private void onCommandJrrpExec (Update event) {
		final double jrrp = MornyJrrp.getJrrpFromTelegramUser(event.message().from(), System.currentTimeMillis());
		final String endChar = jrrp>70 ? "!" : jrrp>30 ? ";" : "...";
		MornyCoeur.getAccount().execute(new SendMessage(
				event.message().chat().id(),
				String.format(
						"<a href='tg://user?id=%d'>%s</a> 在(utc的)今天的运气指数是———— <code>%.2f%%</code> %s",
						event.message().from().id(),
						escapeHtmlTelegram(event.message().from().firstName()),
						jrrp, escapeHtmlTelegram(endChar)
				)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
	}
	
	/**
	 * @since 0.4.3.0
	 */
	private void onSaveDataExec (Update event) {
		if (MornyCoeur.trustedInstance().isTrusted(event.message().from().id())) {
			logger.info(String.format("called save from command by @%s.", event.message().from().username()));
			MornyCoeur.callSaveData();
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_SAVED
					).replyToMessageId(event.message().messageId())
			);
		} else {
			MornyCoeur.getAccount().execute(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_403
					).replyToMessageId(event.message().messageId())
			);
			logger.info("403 call save tag from user @" + event.message().from().username());
		}
	}
	
}
