package cc.sukazyo.cono.morny.util;

import com.pengrad.telegrambot.model.User;

import static cc.sukazyo.untitled.util.telegram.formatting.MsgEscape.escapeHtml;

public class TelegramUserInformation {
	
	public static String informationOutputHTML (User user) {
		
		final StringBuilder userInformation = new StringBuilder();
		userInformation.append(String.format(
				"""
				userid :
				- <code>%d</code>""",
				user.id()
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
		
		return userInformation.toString();
		
	}
	
}
