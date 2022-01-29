package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.untitled.util.telegram.object.InputCommand;

import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;

public class OnTelegramCommand extends EventListener {
	
	@Override
	public boolean onMessage (@Nonnull Update event) {
		if (event.message().text() == null) {
			return false; // 检测到无消息文本，忽略掉命令处理
		}
		final InputCommand command = new InputCommand(event.message().text());
		if (command.getTarget() != null && !MornyCoeur.getUsername().equals(command.getTarget())) {
			return true; // 检测到命令并非针对 morny，退出整个事件处理链
		}
		return MornyCoeur.commandManager().execute(command, event); // 转交命令管理器执行命令
	}
	
}
