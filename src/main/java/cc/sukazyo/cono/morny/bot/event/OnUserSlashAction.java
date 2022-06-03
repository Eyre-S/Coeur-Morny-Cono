package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.util.tgapi.TGToStringFromMessage;
import cc.sukazyo.untitled.util.command.CommonCommand;
import cc.sukazyo.untitled.util.string.StringArrays;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;

import static cc.sukazyo.untitled.util.telegram.formatting.MsgEscape.escapeHtml;

public class OnUserSlashAction extends EventListener {
	
	@Override
	public boolean onMessage (@Nonnull Update event) {
		final String text = event.message().text();
		if (text == null) return false;
		
		if (text.startsWith("/"))
		{
			
			/// Due to @Lapis_Apple, we stopped slash action function at .DP7 groups.
			/// It may be enabled after some updates when the function will not be conflicted to other bots.
			// if (event.message().chat().id() == ) return false;
//{			if (event.message().chat().title() != null && event.message().chat().title().contains(".DP7")) {
//				logger.info(String.format("""
//					Chat slash action ignored due to the following keyword.
//					 - %s
//					 - ".DP7\"""",
//						TGToString.as(event.message().chat()).toStringFullNameId()
//				));
//				return false;
//			}
			
			final String[] action = CommonCommand.format(text);
			action[0] = action[0].substring(1);
			
			if (action[0].matches("^\\w+(@\\w+)?$")) {
				return false; // 忽略掉 Telegram 命令格式的输入
			} else if (action[0].contains("/")) {
				return false; // 忽略掉疑似目录格式的输入
			}
			
			final boolean isHardParse = "".equals(action[0]);
			/* 忽略空数据 */ if (isHardParse && action.length < 2) { return false; }
			final String verb = isHardParse ? action[1] : action[0];
			final boolean hasObject = action.length != (isHardParse?2:1);
			final String object =
					hasObject ?
					StringArrays.connectStringArray(action, " ", isHardParse?2:1, action.length-1) :
					"";
			final Message origin = event.message();
			final Message target = (event.message().replyToMessage() == null ? (
					origin
			): (
					event.message().replyToMessage()
			));
			
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					String.format(
							"%s %s%s %s %s!",
							TGToStringFromMessage.as(origin).getSenderFirstNameRefHtml(),
							escapeHtml(verb), escapeHtml((hasObject?"":"了")),
							origin==target ?
									"<a href='tg://user?id="+TGToStringFromMessage.as(target).getSenderId()+"'>自己</a>" :
							TGToStringFromMessage.as(target).getSenderFirstNameRefHtml(),
							escapeHtml(hasObject ? object+" " : "")
					)
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
			
			return true;
			
		}
		return false;
	}
	
}
