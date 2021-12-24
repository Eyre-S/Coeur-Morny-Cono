package cc.sukazyo.cono.morny.bot.api;

import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
public abstract class EventListener {
	
	public boolean onMessage (@Nonnull Update update) {
		return false;
	}
	
	public boolean onEditedMessage (@Nonnull Update update) {
		return false;
	}
	
	public boolean onChannelPost (@Nonnull Update update) {
		return false;
	}
	
	public boolean onEditedChannelPost (@Nonnull Update update) {
		return false;
	}
	
	public boolean onInlineQuery (@Nonnull Update update) {
		return false;
	}
	
	public boolean onChosenInlineResult (@Nonnull Update update) {
		return false;
	}
	
	public boolean onCallbackQuery (@Nonnull Update update) {
		return false;
	}
	
	public boolean onShippingQuery (@Nonnull Update update) {
		return false;
	}
	
	public boolean onPreCheckoutQuery (@Nonnull Update update) {
		return false;
	}
	
	public boolean onPoll (@Nonnull Update update) {
		return false;
	}
	
	public boolean onPollAnswer (@Nonnull Update update) {
		return false;
	}
	
	public boolean onMyChatMemberUpdated (@Nonnull Update update) {
		return false;
	}
	
	public boolean onChatMemberUpdated (@Nonnull Update update) {
		return false;
	}
	
}
