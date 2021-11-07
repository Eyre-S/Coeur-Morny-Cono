package cc.sukazyo.cono.morny.bot.api;

import com.pengrad.telegrambot.model.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static cc.sukazyo.cono.morny.Logger.logger;

public class EventListenerManager {
	
	private static final List<EventListener> listeners = new ArrayList<>();
	
	private static class EventPublisher extends Thread {
		
		private final Function<EventListener, Boolean> exec;
		
		public EventPublisher(Update update, Function<EventListener, Boolean> exec) {
			this.setName("EVT"+update.updateId());
			this.exec = exec;
		}
		
		@Override
		public void run () {
			for (EventListener x : listeners) {
				if (exec.apply(x)) return;
			}
			logger.info("event exited undone");
		}
		
	}
	
	public static void addListener (EventListener... listeners) {
		EventListenerManager.listeners.addAll(Arrays.asList(listeners));
	}
	
	public static void publishMessageEvent (Update update) {
		new EventPublisher(update, x -> x.onMessage(update)).start();
	}
	
	public static void publishEditedMessageEvent (Update update) {
		new EventPublisher(update, x -> x.onEditedMessage(update)).start();
	}
	
	public static void publishChannelPostEvent (Update update) {
		new EventPublisher(update, x -> x.onChannelPost(update)).start();
	}
	
	public static void publishEditedChannelPostEvent (Update update) {
		new EventPublisher(update, x -> x.onEditedChannelPost(update)).start();
	}
	
	public static void publishInlineQueryEvent (Update update) {
		new EventPublisher(update, x -> x.onInlineQuery(update)).start();
	}
	
	public static void publishChosenInlineResultEvent (Update update) {
		new EventPublisher(update, x -> x.onChosenInlineResult(update)).start();
	}
	
	public static void publishCallbackQueryEvent (Update update) {
		new EventPublisher(update, x -> x.onCallbackQuery(update)).start();
	}
	
	public static void publishShippingQueryEvent (Update update) {
		new EventPublisher(update, x -> x.onShippingQuery(update)).start();
	}
	
	public static void publishPreCheckoutQueryEvent (Update update) {
		new EventPublisher(update, x -> x.onPreCheckoutQuery(update)).start();
	}
	
	public static void publishPollEvent (Update update) {
		new EventPublisher(update, x -> x.onPoll(update)).start();
	}
	
	public static void publishPollAnswerEvent (Update update) {
		new EventPublisher(update, x -> x.onPollAnswer(update)).start();
	}
	
	public static void publishMyChatMemberUpdatedEvent (Update update) {
		new EventPublisher(update, x -> x.onMyChatMemberUpdated(update)).start();
	}
	
	public static void publishChatMemberUpdatedEvent (Update update) {
		new EventPublisher(update, x -> x.onChatMemberUpdated(update)).start();
	}
	
}
