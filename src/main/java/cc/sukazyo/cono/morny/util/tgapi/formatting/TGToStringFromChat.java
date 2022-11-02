package cc.sukazyo.cono.morny.util.tgapi.formatting;

import com.pengrad.telegrambot.model.Chat;

public class TGToStringFromChat {
	
	private final Chat data;
	
	public TGToStringFromChat(Chat chat) {
		this.data = chat;
	}
	
	public String toStringFullNameId() {
		if (data.title() == null) {
			throw new IllegalArgumentException("Cannot format private chat to group Name+Id format.");
		}
		return (data.username() == null) ?
			   (String.format("%s [%d]", data.title(), data.id())) :
			   (String.format("%s {%s}[%d]", data.title(), data.username(), data.id()));
	}
	
}
