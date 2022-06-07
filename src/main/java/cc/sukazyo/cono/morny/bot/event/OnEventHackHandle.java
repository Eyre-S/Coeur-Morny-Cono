package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.untitled.util.telegram.formatting.MsgEscape;

import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

import static cc.sukazyo.cono.morny.Log.logger;

/**
 * 事件劫持与序列化工具.
 * @since 0.4.2.0
 */
public class OnEventHackHandle extends EventListener {
	
	/** 事件劫持请求列表 */
	private static final Map<String, Hacker> hackers = new HashMap<>();
	
	/**
	 * 触发事件劫持的限定条件.
	 * @since 0.4.2.0
	 */
	public enum HackType {
		/** 只有相同用户发起的事件才会被触发 */
		USER,
		/** 只有相同群组内发生的事件才会触发 */
		GROUP,
		/** 任何事件都可以触发 */
		ANY
	}
	
	public record Hacker(long fromChatId, long fromMessageId) {
		@Override public String toString() {
			return fromChatId + "/" + fromMessageId;
		}
	}
	
	/**
	 * @since 0.4.2.0
	 */
	public static void registerHack(long fromMessageId, long fromUserId, long fromChatId, @Nonnull HackType type) {
		String rec = null;
		switch (type) {
			case USER -> rec = String.format("((%d))", fromUserId);
			case GROUP -> rec = String.format("{{%d}}", fromChatId);
			case ANY -> rec = "[[]]";
		}
		hackers.put(rec, new Hacker(fromChatId, fromMessageId));
		logger.debug("add hacker track " + rec);
	}
	
	private boolean onEventHacked (Update update, long chatId, long fromUser) {
		logger.debug(String.format("got event signed {{%d}}((%d))", chatId, fromUser));
		Hacker x;
		x = hackers.remove(String.format("((%d))", fromUser));
		if (x == null) x = hackers.remove(String.format("{{%d}}", chatId));
		if (x == null) x = hackers.remove("[[]]");
		if (x == null) return false;
		logger.debug("hacked event by " + x);
		MornyCoeur.extra().exec(new SendMessage(x.fromChatId, String.format(
				"<code>%s</code>",
				MsgEscape.escapeHtml(new GsonBuilder().setPrettyPrinting().create().toJson(update))
		)).parseMode(ParseMode.HTML).replyToMessageId((int)x.fromMessageId));
		return true;
	}
	
	@Override
	public boolean onMessage (@Nonnull Update update) {
		return onEventHacked(update, update.message().chat().id(), update.message().from().id());
	}
	
	@Override
	public boolean onEditedMessage (@Nonnull Update update) {
		return onEventHacked(update, update.editedMessage().chat().id(), update.editedMessage().from().id());
	}
	
	@Override
	public boolean onChannelPost (@Nonnull Update update) {
		return onEventHacked(update, update.channelPost().chat().id(), update.channelPost().chat().id());
	}
	
	@Override
	public boolean onEditedChannelPost (@Nonnull Update update) {
		return onEventHacked(update, update.editedChannelPost().chat().id(), update.editedChannelPost().chat().id());
	}
	
	@Override
	public boolean onInlineQuery (@Nonnull Update update) {
		return onEventHacked(update, 0, update.inlineQuery().from().id());
	}
	
	@Override
	public boolean onChosenInlineResult (@Nonnull Update update) {
		return onEventHacked(update, 0, update.chosenInlineResult().from().id());
	}
	
	@Override
	public boolean onCallbackQuery (@Nonnull Update update) {
		return onEventHacked(update, 0, update.callbackQuery().from().id());
	}
	
	@Override
	public boolean onShippingQuery (@Nonnull Update update) {
		return onEventHacked(update, 0, update.shippingQuery().from().id());
	}
	
	@Override
	public boolean onPreCheckoutQuery (@Nonnull Update update) {
		return onEventHacked(update, 0, update.preCheckoutQuery().from().id());
	}
	
	@Override
	public boolean onPoll (@Nonnull Update update) {
		return onEventHacked(update, 0, 0);
	}
	
	@Override
	public boolean onPollAnswer (@Nonnull Update update) {
		return onEventHacked(update, 0, update.pollAnswer().user().id());
	}
	
	@Override
	public boolean onMyChatMemberUpdated (@Nonnull Update update) {
		return onEventHacked(update, update.myChatMember().chat().id(), update.myChatMember().from().id());
	}
	
	@Override
	public boolean onChatMemberUpdated (@Nonnull Update update) {
		return onEventHacked(update, update.chatMember().chat().id(), update.chatMember().from().id());
	}
	
	@Override
	public boolean onChatJoinRequest (@Nonnull Update update) {
		return onEventHacked(update, update.chatJoinRequest().chat().id(), update.chatJoinRequest().from().id());
	}
	
}
