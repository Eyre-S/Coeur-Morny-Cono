package cc.sukazyo.cono.morny.util.tgapi.formatting;

import com.pengrad.telegrambot.model.User;

public class TGToStringFromUser {
	
	private final User data;
	
	public TGToStringFromUser (User user) {
		this.data = user;
	}
	
	public String fullname () {
		return data.firstName() + (data.lastName()==null ? "" : " "+data.lastName());
	}
	
	public String fullnameRefHtml () {
		return String.format(
				"<a href='tg://user?id=%d'>%s</a>",
				data.id(),
				MsgEscape.escapeHtml(fullname())
		);
	}
	
	public String fullnameRefMarkdown () {
		return String.format(
				"[%s](tg://user?id=%d)",
				fullname(),
				data.id()
		);
	}
	
	public String firstnameRefHtml () {
		return String.format(
				"<a href='tg://user?id=%d'>%s</a>",
				data.id(),
				MsgEscape.escapeHtml(data.firstName())
		);
	}
	
	public String firstnameRefMarkdown () {
		return String.format(
				"[%s](tg://user?id=%d)",
				data.firstName(),
				data.id()
		);
	}
	
	public String toStringLogTag () {
		return (data.username()==null ? fullname()+" " : "@"+data.username()) + "[" + data.id() + "]";
	}
	
}
