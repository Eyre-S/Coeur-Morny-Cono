package cc.sukazyo.cono.morny.util.tgapi.formatting;

import com.pengrad.telegrambot.model.Chat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TGToStringFromChat {
	
	
	public static final long MASK_BOTAPI_ID = -1000000000000L;
	
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
	
	@Nonnull
	public String getSafeName () {
		if (data.type() == Chat.Type.Private)
			return data.firstName() + (data.lastName()==null ? "" : " "+data.lastName());
		else return data.title();
	}
	
	@Nullable
	public String getSafeLinkHTML () {
		if (data.username() == null) {
			if (data.type() == Chat.Type.Private)
				// language=html
				return String.format("<a href='tg://user?id=%d'>@[u:%d]</a>", data.id(), data.id());
			// language=html
			else return String.format("<a href='https://t.me/c/%d'>@[c/%d]</a>", id_tdLib(), id_tdLib());
		} else return "@"+data.username();
	}
	
	public long id_tdLib () {
		return data.id() < 0 ? Math.abs(data.id() - MASK_BOTAPI_ID) : data.id();
	}
	
	@Nonnull
	public String getTypeTag () {
		return switch (data.type()) {
			case Private -> "🔒";
			case group -> "💭";
			case supergroup -> "💬";
			case channel -> "📢";
		};
	}
	
}
