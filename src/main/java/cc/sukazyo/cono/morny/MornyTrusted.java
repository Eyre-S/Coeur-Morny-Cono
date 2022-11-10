package cc.sukazyo.cono.morny;

import com.pengrad.telegrambot.model.ChatMember.Status;

import java.util.Set;

/**
 * 对用户进行身份权限验证的管理类
 */
public class MornyTrusted {
	
	private final MornyCoeur instance;
	
	public MornyTrusted (MornyCoeur instance) {
		this.instance = instance;
	}
	
	/**
	 * 用于检查一个 telegram-user 是否受信任<br>
	 * <br>
	 * 用户需要受信任才能执行一些对程序甚至是宿主环境而言危险的操作，例如关闭程序<br>
	 * <br>
	 * 它的逻辑(目前)是检查群聊 {@link MornyConfig#trustedChat} 中这个用户是否为群组管理员
	 *
	 * @param userId 需要检查的用户的id
	 * @return 所传递的用户id对应的用户是否受信任
	 */
	public boolean isTrusted (long userId) {
		if (userId == instance.config.trustedMaster) return true;
		if (instance.config.trustedChat == -1) return false;
		return MornyCoeur.extra().isUserInGroup(userId, instance.config.trustedChat, Status.administrator);
	}
	
	public boolean isTrustedForDinnerRead (long userId) {
		return instance.config.dinnerTrustedReaders.contains(userId);
	}
	
	public Set<Long> getTrustedReadersOfDinnerSet () {
		return Set.copyOf(instance.config.dinnerTrustedReaders);
	}
	
}
