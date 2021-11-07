package cc.sukazyo.cono.morny.bot.api;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.util.List;

public class OnUpdate {
	
	public static int onNormalUpdate (List<Update> updates) {
		for (Update update : updates) {
			if (update.message() != null) {
				EventListenerManager.publishMessageEvent(update);
			}
			if (update.editedMessage() != null) {
				EventListenerManager.publishEditedMessageEvent(update);
			}
			if (update.channelPost() != null) {
				EventListenerManager.publishChannelPostEvent(update);
			}
			if (update.editedChannelPost() != null) {
				EventListenerManager.publishEditedChannelPostEvent(update);
			}
			if (update.inlineQuery() != null) {
				EventListenerManager.publishInlineQueryEvent(update);
			}
			if (update.chosenInlineResult() != null) {
				EventListenerManager.publishChosenInlineResultEvent(update);
			}
			if (update.callbackQuery() != null) {
				EventListenerManager.publishCallbackQueryEvent(update);
			}
			if (update.shippingQuery() != null) {
				EventListenerManager.publishShippingQueryEvent(update);
			}
			if (update.preCheckoutQuery() != null) {
				EventListenerManager.publishPreCheckoutQueryEvent(update);
			}
			if (update.poll() != null) {
				EventListenerManager.publishPollEvent(update);
			}
			if (update.pollAnswer() != null) {
				EventListenerManager.publishPollAnswerEvent(update);
			}
			if (update.myChatMember() != null) {
				EventListenerManager.publishMyChatMemberUpdatedEvent(update);
			}
			if (update.chatMember() != null) {
				EventListenerManager.publishChatMemberUpdatedEvent(update);
			}
		}
		return UpdatesListener.CONFIRMED_UPDATES_ALL;
	}
	
}
