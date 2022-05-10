package cc.sukazyo.cono.morny.util.tgapi;

import cc.sukazyo.untitled.telegram.api.formatting.TGToString;
import cc.sukazyo.untitled.util.telegram.formatting.MsgEscape;
import com.pengrad.telegrambot.model.Message;

import javax.annotation.Nonnull;

public class TGToStringFromMessage extends TGToString {
	
	@Nonnull
	private final Message message;
	
	public TGToStringFromMessage (@Nonnull Message message) { this.message = message; }
	public static TGToStringFromMessage as (@Nonnull Message message) { return new TGToStringFromMessage(message); }
	
	@Nonnull
	public String getSenderFirstNameRefHtml () {
		return message.senderChat()==null ? TGToString.as(message.from()).firstnameRefHtml() : String.format(
				"<a href='tg://user?id=%d'>%s</a>",
				message.senderChat().id(),
				MsgEscape.escapeHtml(message.senderChat().title())
		);
	}
	
	public long getSenderId () {
		return message.senderChat()==null ? message.from().id() : message.senderChat().id();
	}
	
}
