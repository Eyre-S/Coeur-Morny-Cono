package cc.sukazyo.cono.morny.bot.event.on_commands;

import cc.sukazyo.cono.morny.MornyCoeur;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatMemberResponse;

import javax.annotation.Nonnull;

public class GetUsernameAndId {
	
	public static void exec (@Nonnull String[] command, @Nonnull Update event) {
		
		if (command.length > 2) { MornyCoeur.getAccount().execute(new SendMessage(
				event.message().chat().id(),
				"[Unavailable] Too much arguments."
		).replyToMessageId(event.message().messageId())); return; }
		
		long userId = 0;
		
		if ( event.message().replyToMessage()!= null) {
			userId = event.message().replyToMessage().from().id();
		}
		if (command.length > 1) {
			try {
				userId = Long.parseLong(command[1]);
			} catch (NumberFormatException e) {
				MornyCoeur.getAccount().execute(new SendMessage(
						event.message().chat().id(),
						"[Unavailable] " + e.getMessage()
				).replyToMessageId(event.message().messageId()));
				return;
			}
		}
		
		if (userId == 0) {
			MornyCoeur.getAccount().execute(new SendMessage(
					event.message().chat().id(),
					"[Unavailable] no userid given."
			).replyToMessageId(event.message().messageId()));
			return;
		}
		
		final GetChatMemberResponse response = MornyCoeur.getAccount().execute(
				new GetChatMember(event.message().chat().id(), userId)
		);
		
		if (response.chatMember() == null) {
			MornyCoeur.getAccount().execute(new SendMessage(
					event.message().chat().id(),
					"[Unavailable] user not found."
			).replyToMessageId(event.message().messageId()));
			return;
		}
		
		final User user = response.chatMember().user();
		
		final StringBuilder userInformation = new StringBuilder();
		userInformation.append(String.format(
						"userid :\n" +
						"- <code>%d</code>\n" +
						"username :\n" +
						"- <code>%s</code>",
						userId, user.username()
		));
		if (user.firstName() == null) {
			userInformation.append("\nfirstname : <u>null</u>");
		} else {
			userInformation.append(String.format(
					"\nfirstname :\n" +
					"- <code>%s</code>",
					user.firstName()
			));
		}
		if (user.lastName() == null) {
			userInformation.append("\nlastname : <u>null</u>");
		} else {
			userInformation.append(String.format(
					"\nlastname :\n" +
					"- <code>%s</code>",
					user.lastName()
			));
		}
		if (user.languageCode() != null) {
			userInformation.append(String.format(
					"\nlanguage-code :\n" +
					"- <code>%s</code>",
					user.languageCode()
			));
		}
		
		MornyCoeur.getAccount().execute(new SendMessage(
				event.message().chat().id(),
				userInformation.toString()
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
		
	}
	
}
