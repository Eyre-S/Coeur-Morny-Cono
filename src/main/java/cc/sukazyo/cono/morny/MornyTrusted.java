package cc.sukazyo.cono.morny;

import com.pengrad.telegrambot.model.ChatMember;
import com.pengrad.telegrambot.request.GetChatMember;

/**
 * 对用户进行身份权限验证的管理类
 */
public class MornyTrusted {
	
	/**
	 * 群聊id，其指向的群聊指示了哪个群的成员是受信任的
	 * @see #isTrusted(long) 受信检查
	 */
	public final Long TRUSTED_CHAT_ID;
	
	/**
	 * morny 的主人<br>
	 * 这项值的对象总是会被认为是可信任的
	 */
	public final long MASTER;
	
	public MornyTrusted (long master, long trustedChatId) {
		this.TRUSTED_CHAT_ID = trustedChatId;
		this.MASTER = master;
	}
	
	/**
	 * 用于检查一个 telegram-user 是否受信任<br>
	 * <br>
	 * 用户需要受信任才能执行一些对程序甚至是宿主环境而言危险的操作，例如关闭程序<br>
	 * <br>
	 * 它的逻辑(目前)是检查群聊 {@link #TRUSTED_CHAT_ID} 中这个用户是否为群组管理员
	 *
	 * @param userId 需要检查的用户的id
	 * @return 所传递的用户id对应的用户是否受信任
	 */
	public boolean isTrusted (long userId) {
		if (userId == MASTER) return true;
		final ChatMember chatMember = MornyCoeur.getAccount().execute(new GetChatMember(TRUSTED_CHAT_ID, userId)).chatMember();
		return (
				chatMember != null && (
						chatMember.status() == ChatMember.Status.administrator ||
						chatMember.status() == ChatMember.Status.creator
				)
		);
	}
	
}
