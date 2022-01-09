package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.bot.api.Executor;
import cc.sukazyo.untitled.util.command.CommonCommand;
import cc.sukazyo.untitled.util.string.StringArrays;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;

import static cc.sukazyo.untitled.util.telegram.formatting.MsgEscape.escapeHtml;

public class OnUserSlashAction extends EventListener {
	
	@Override
	public boolean onMessage (@Nonnull Update event) {
		final String text = event.message().text();
		if (text == null) return false;
		
		if (text.startsWith("/")) {
			int prefixLength = 1;
			boolean useVerbSuffix = true;
			boolean useObjectPrefix = true;
			if (text.startsWith("//#") || text.startsWith("///")) {
				useVerbSuffix = false;
				useObjectPrefix = false;
				prefixLength = 3;
			} else if (text.startsWith("/#")) {
				useObjectPrefix = false;
				prefixLength = 2;
			} else if (text.startsWith("//")) {
				useVerbSuffix = false;
				prefixLength = 2;
			}
			
			final String[] action = CommonCommand.format(text.substring(prefixLength));
			final String verb = action[0];
			final boolean hasObject = action.length != 1;
			final String object = StringArrays.connectStringArray(action, " ", 1, action.length-1);
			final User origin = event.message().from();
			final User target = (event.message().replyToMessage() == null ? (
					origin
			): (
					event.message().replyToMessage().from()
			));
			
			Executor.as(MornyCoeur.getAccount()).exec(new SendMessage(
					event.message().chat().id(),
					String.format(
							"<a href='tg://user?id=%d'>%s</a> %s%s <a href='tg://user?id=%d'>%s</a>%s%s",
							origin.id(), escapeHtml(origin.firstName()),
							verb, escapeHtml((useVerbSuffix?"了":"")),
							target.id(), escapeHtml((origin==target ? "自己" : target.firstName())),
							escapeHtml((hasObject ? (useObjectPrefix ?" 的": " ") : "")),
							escapeHtml((hasObject ? object : ""))
					)
			).parseMode(ParseMode.HTML));
			
			return true;
			
		}
		return false;
	}
	
}
