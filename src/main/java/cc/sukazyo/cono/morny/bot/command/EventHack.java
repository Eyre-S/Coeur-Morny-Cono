package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornyTrusted;
import cc.sukazyo.cono.morny.bot.event.OnEventHackHandle;
import cc.sukazyo.cono.morny.data.TelegramStickers;

import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendSticker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link OnEventHackHandle} 的命令行前端
 * @since 0.4.2.0
 */
public class EventHack implements ITelegramCommand {
	
	@Nonnull @Override public String getName () { return "event_hack"; }
	@Nullable @Override public String[] getAliases () { return null; }
	@Nonnull @Override public String getParamRule () { return "[(user|group|any)]"; }
	@Nonnull @Override public String getDescription () { return "输出 bot 下一个获取到的事件序列化数据"; }
	
	/**
	 * {@link OnEventHackHandle} 的命令行前端<br>
	 * <br>
	 * 实现了通过命令行进行 EventHack 功能。<br>
	 * 支持三种模式，默认为 {@link OnEventHackHandle.HackType#USER USER}，
	 * {@link OnEventHackHandle.HackType#ANY ANY} 时，将会通过 {@link MornyTrusted#isTrusted(long)} 检查触发用户的权限
	 *
	 * @param event 命令基础参数，触发的事件对象本身
	 * @param command 命令基础参数，解析出的命令对象
	 * @since 0.4.2.0
	 */
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		boolean isOk = false;
		
		String x_mode = "";
		if (command.hasArgs()) {
			x_mode = command.getArgs()[0];
		}
		
		switch (x_mode) {
			case "any":
				if (MornyCoeur.trustedInstance().isTrusted(event.message().from().id())) {
					OnEventHackHandle.registerHack(
							event.message().messageId(),
							event.message().from().id(),
							event.message().chat().id(),
							OnEventHackHandle.HackType.ANY
					);isOk = true;
				}
				break;
			case "group":
				OnEventHackHandle.registerHack(
						event.message().messageId(),
						event.message().from().id(),
						event.message().chat().id(),
						OnEventHackHandle.HackType.GROUP
				);isOk = true;
				break;
			default:
				OnEventHackHandle.registerHack(
						event.message().messageId(),
						event.message().from().id(),
						event.message().chat().id(),
						OnEventHackHandle.HackType.USER
				);isOk = true;
				break;
		}
		
		if (isOk) {
			MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_WAITING
					).replyToMessageId(event.message().messageId())
			);
		} else {
			MornyCoeur.extra().exec(new SendSticker(
							event.message().chat().id(),
							TelegramStickers.ID_403
					).replyToMessageId(event.message().messageId())
			);
		}
		
	}
	
}
