package cc.sukazyo.cono.morny.util.tgapi;

import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.response.BaseResponse;

public class ExtraAction {
	
	private final TelegramBot bot;
	
	public ExtraAction (TelegramBot bot) {
		this.bot = bot;
	}
	
	public static ExtraAction as (TelegramBot bot) {
		return new ExtraAction(bot);
	}
	
	public boolean isUserInGroup (User user, Chat chat) {
		return isUserInGroup(user.id(), chat.id());
	}
	
	public <T extends BaseRequest<T, R>, R extends BaseResponse> R exec (T req) {
		return exec(req, "");
	}
	
	public <T extends BaseRequest<T, R>, R extends BaseResponse> R exec (T req, String errorMessage) {
		final R resp = bot.execute(req);
		if (!resp.isOk()) throw new EventRuntimeException.ActionFailed(
				(errorMessage.isEmpty() ? String.valueOf(resp.errorCode()) : errorMessage),
				resp
		);
		return resp;
	}
	
	public boolean isUserInGroup (User user, Chat chat, ChatMember.Status permissionLevel) {
		return isUserInGroup(user.id(), chat.id(), permissionLevel);
	}
	
	public boolean isUserInGroup (long userId, long chatId) {
		return isUserInGroup(userId, chatId, ChatMember.Status.restricted);
	}
	
	public boolean isUserInGroup (long userId, long chatId, ChatMember.Status permissionLevel) {
		final ChatMember chatMember = exec(new GetChatMember(chatId, userId)).chatMember();
		return
				chatMember != null &&
						UserPermissionLevel.as(chatMember.status()).hasPermission(UserPermissionLevel.as(permissionLevel));
	}
	
}

enum UserPermissionLevel {
	
	CREATOR(3),
	ADMINISTRATOR(2),
	MEMBER(1),
	RESTRICTED(0),
	LEFT(-1),
	KICKED(-2);
	
	final int permissionLevel;
	
	UserPermissionLevel (int permissionLevel) {
		this.permissionLevel = permissionLevel;
	}
	
	static UserPermissionLevel as (ChatMember.Status status) {
		return switch (status) {
			case creator -> CREATOR;
			case administrator -> ADMINISTRATOR;
			case member -> MEMBER;
			case restricted -> RESTRICTED;
			case left -> LEFT;
			case kicked -> KICKED;
		};
	}
	
	boolean hasPermission (UserPermissionLevel required) {
		return this.permissionLevel >= required.permissionLevel;
	}
	
}
