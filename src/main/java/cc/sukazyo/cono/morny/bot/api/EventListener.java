package cc.sukazyo.cono.morny.bot.api;

import com.pengrad.telegrambot.model.Update;

@SuppressWarnings("unused")
public abstract class EventListener {
	
	public boolean onMessage (Update update) {
		return false;
	}
	
	public boolean onEditedMessage (Update update) {
		return false;
	}
	
	public boolean onChannelPost (Update update) {
		return false;
	}
	
	public boolean onEditedChannelPost (Update update) {
		return false;
	}
	
	public boolean onInlineQuery (Update update) {
		return false;
	}
	
	public boolean onChosenInlineResult (Update update) {
		return false;
	}
	
	public boolean onCallbackQuery (Update update) {
		return false;
	}
	
	public boolean onShippingQuery (Update update) {
		return false;
	}
	
	public boolean onPreCheckoutQuery (Update update) {
		return false;
	}
	
	public boolean onPoll (Update update) {
		return false;
	}
	
	public boolean onPollAnswer (Update update) {
		return false;
	}
	
	public boolean onMyChatMemberUpdated (Update update) {
		return false;
	}
	
	public boolean onChatMemberUpdated (Update update) {
		return false;
	}
	
}
