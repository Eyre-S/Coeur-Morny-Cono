package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.bot.command.ISimpleCommand;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class OnUniMeowTrigger extends EventListener {
	
	private static final Map<String, ISimpleCommand> triggers = new HashMap<>();
	
	public static void register (ISimpleCommand... list) {
		for (ISimpleCommand cmd : list)
			triggers.put(cmd.getName(), cmd);
	}
	
	@Override
	public boolean onMessage (@Nonnull Update event) {
		if (event.message().text() == null) return false;
		AtomicBoolean ok = new AtomicBoolean(false);
		triggers.forEach((name, command) -> {
			name = "/" + name;
			if (name.equals(event.message().text())) {
				command.execute(new InputCommand(name), event);
				ok.set(true);
			}
		});
		return ok.get();
	}
	
}
