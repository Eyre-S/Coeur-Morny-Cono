package cc.sukazyo.cono.morny.bot.event.on_commands;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.NbnhhshQuery;
import cc.sukazyo.untitled.util.string.StringArrays;
import cc.sukazyo.untitled.util.telegram.object.InputCommand;

import static cc.sukazyo.untitled.util.telegram.formatting.MsgEscape.escapeHtml;

public class Nbnhhsh {
	
	public static void exec (Update event, InputCommand command) {
		
		try {
			
			String queryTarget = "";
			if (event.message().replyToMessage() != null && event.message().replyToMessage().text() != null)
				queryTarget = event.message().replyToMessage().text();
			if (command.hasArgs())
				queryTarget = StringArrays.connectStringArray(command.getArgs(), " ", 0, command.getArgs().length-1);
			
			NbnhhshQuery.GuessResult response = NbnhhshQuery.sendGuess(queryTarget);
			
			StringBuilder message = new StringBuilder("<a href=\"https://lab.magiconch.com/nbnhhsh/\">## Result of nbnhhsh query :</a>");
			
			for (NbnhhshQuery.Word word : response.words) {
				if (word.trans == null) continue;
				message.append("\n\n<b>[[ ").append(escapeHtml(word.name)).append(" ]]</b>");
				for (String trans : word.trans) {
					message.append("\n* <i>").append(escapeHtml(trans)).append("</i>");
				}
			}
			
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					message.toString()
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
			
		} catch (Exception e) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"[Exception] in query:\n<code>" + escapeHtml(e.getMessage()) + "</code>"
			).parseMode(ParseMode.HTML).replyToMessageId(event.message().messageId()));
		}
		
	}
	
}
