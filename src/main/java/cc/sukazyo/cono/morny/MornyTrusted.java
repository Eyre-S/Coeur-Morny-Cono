package cc.sukazyo.cono.morny;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.request.GetChatMember;

public class MornyTrusted {
	
	public static final long TRUSTED_CHAT_ID = -1001541451710L;
	
	public static boolean isTrusted (long userId) {
		final ChatMember chatMember = MornyCoeur.getAccount().execute(new GetChatMember(TRUSTED_CHAT_ID, userId)).chatMember();
		return (
				chatMember != null && (
						chatMember.status() == ChatMember.Status.administrator ||
						chatMember.status() == ChatMember.Status.creator
				)
		);
	}
	
}
