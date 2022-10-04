package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.data.NbnhhshQuery;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static cc.sukazyo.cono.morny.util.CommonConvert.stringsConnecting;
import static cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml;

public class Nbnhhsh implements ITelegramCommand {
	
	@Nonnull @Override public String getName () { return "nbnhhsh"; }
	@Nullable @Override public String[] getAliases () { return null; }
	@Nonnull @Override public String getParamRule () { return "[text]"; }
	@Nonnull @Override public String getDescription () { return "检索文本内 nbnhhsh 词条"; }
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		try {
			
			String queryTarget = "";
			if (event.message().replyToMessage() != null && event.message().replyToMessage().text() != null)
				queryTarget = event.message().replyToMessage().text();
			if (command.hasArgs())
				queryTarget = stringsConnecting(command.getArgs(), " ", 0, command.getArgs().length-1);
			
			NbnhhshQuery.GuessResult response = NbnhhshQuery.sendGuess(queryTarget);
			
			StringBuilder message = new StringBuilder("<a href=\"https://lab.magiconch.com/nbnhhsh/\">## Result of nbnhhsh query :</a>");
			
			for (NbnhhshQuery.Word word : response.words) {
				if (word.trans != null && word.trans.length == 0) word.trans = null;
				if (word.inputting != null && word.inputting.length == 0) word.inputting = null;
				if (word.trans == null && word.inputting == null) continue;
				message.append("\n\n<b>[[ ").append(escapeHtml(word.name)).append(" ]]</b>");
				if (word.trans != null) for (String trans : word.trans) {
					message.append("\n* <i>").append(escapeHtml(trans)).append("</i>");
				}
				if (word.inputting != null) {
					if (word.trans != null) message.append("\n");
					message.append(" maybe:");
					for (String trans : word.inputting) {
						message.append("\n` <i>").append(escapeHtml(trans)).append("</i>");
					}
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
