package cc.sukazyo.cono.morny.bot.event.on_commands;

import cc.sukazyo.cono.morny.MornyCoeur;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatMemberResponse;

import javax.annotation.Nonnull;

import static cc.sukazyo.untitled.util.telegram.formatting.MsgEscape.escapeHtml;

public class GetUsernameAndId {
	
	public static void exec (@Nonnull String[] args, @Nonnull Update event) {
		
		if (args.length > 1) { MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				"[Unavailable] Too much arguments."
		).replyToMessageId(event.message().messageId())); return; }
		
		long userId = event.message().from().id();
		
		if ( event.message().replyToMessage()!= null) {
			userId = event.message().replyToMessage().from().id();
		}
		if (args.length > 0) {
			try {
				userId = Long.parseLong(args[0]);
			} catch (NumberFormatException e) {
				MornyCoeur.extra().exec(new SendMessage(
						event.message().chat().id(),
						"[Unavailable] " + e.getMessage()
				).replyToMessageId(event.message().messageId()));
				return;
			}
		}
		
		final GetChatMemberResponse response = MornyCoeur.extra().exec(
				new GetChatMember(event.message().chat().id(), userId)
		);
		
		if (response.chatMember() == null) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"[Unavailable] user not found."
			).replyToMessageId(event.message().messageId()));
			return;
		}
		
		final User user = response.chatMember().user();
		
		final StringBuilder userInformation = new StringBuilder();
		userInformation.append(String.format(
				"""
				userid :
				- <code>%d</code>""",
				userId
		));
		if (user.username() == null) {
			userInformation.append("\nusername : <u>null</u>");
		} else {
			userInformation.append(String.format(
					"""
					
					username :
					- <code>%s</code>""",
					escapeHtml(user.username())
			));
		}
		if (user.firstName() == null) {
			userInformation.append("\nfirstname : <u>null</u>");
		} else {
			userInformation.append(String.format(
					"""
					
					firstname :
					- <code>%s</code>""",
					escapeHtml(user.firstName())
			));
		}
		if (user.lastName() == null) {
			userInformation.append("\nlastname : <u>null</u>");
		} else {
			userInformation.append(String.format(
					"""
					
					lastname :
					- <code>%s</code>""",
					escapeHtml(user.lastName())
			));
		}
		if (user.languageCode() != null) {
			userInformation.append(String.format(
					"""
					
					language-code :
					- <code>%s</code>""",
					escapeHtml(user.languageCode())
			));
		}
		
		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				userInformation.toString()
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
		
	}
	
}
