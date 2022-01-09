package cc.sukazyo.cono.morny.bot.api;

import com.google.gson.GsonBuilder;
import com.pengrad.telegrambot.model.Update;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static cc.sukazyo.cono.morny.Log.logger;

public class EventListenerManager {
	
	private static final List<EventListener> listeners = new ArrayList<>();
	
	private static class EventPublisher extends Thread {
		
		private final Function<EventListener, Boolean> exec;
		
		public EventPublisher(@Nonnull Update update, @Nonnull Function<EventListener, Boolean> exec) {
			this.setName("EVT"+update.updateId());
			this.exec = exec;
		}
		
		@Override
		public void run () {
			for (EventListener x : listeners) {
				try {
					
					if (exec.apply(x)) return;
					
				} catch (EventRuntimeException e) {
					
					final StringBuilder errorMessage = new StringBuilder();
					errorMessage.append("Event runtime breaks: " + e.getMessage()).append('\n');
					errorMessage.append("at " + e.getStackTrace()[0].toString()).append('\n');
					errorMessage.append("at " + e.getStackTrace()[1].toString()).append('\n');
					errorMessage.append("at " + e.getStackTrace()[2].toString()).append('\n');
					errorMessage.append("at " + e.getStackTrace()[3].toString()).append('\n');
					if (e instanceof EventRuntimeException.ActionFailed) {
						errorMessage.append((
								"\"telegram request track\": " +
								new GsonBuilder().setPrettyPrinting().create().toJson(((EventRuntimeException.ActionFailed)e).getResponse())
						).indent(4)).append('\n');
					}
					
					logger.error(errorMessage.toString());
					
				} catch (Exception e) {
					
					logger.error("Event Error!");
					e.printStackTrace(System.out);
					
				}
			}
		}
		
	}
	
	public static void addListener (@Nonnull EventListener... listeners) {
		EventListenerManager.listeners.addAll(Arrays.asList(listeners));
	}
	
	public static void publishMessageEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onMessage(update)).start();
	}
	
	public static void publishEditedMessageEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onEditedMessage(update)).start();
	}
	
	public static void publishChannelPostEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onChannelPost(update)).start();
	}
	
	public static void publishEditedChannelPostEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onEditedChannelPost(update)).start();
	}
	
	public static void publishInlineQueryEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onInlineQuery(update)).start();
	}
	
	public static void publishChosenInlineResultEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onChosenInlineResult(update)).start();
	}
	
	public static void publishCallbackQueryEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onCallbackQuery(update)).start();
	}
	
	public static void publishShippingQueryEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onShippingQuery(update)).start();
	}
	
	public static void publishPreCheckoutQueryEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onPreCheckoutQuery(update)).start();
	}
	
	public static void publishPollEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onPoll(update)).start();
	}
	
	public static void publishPollAnswerEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onPollAnswer(update)).start();
	}
	
	public static void publishMyChatMemberUpdatedEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onMyChatMemberUpdated(update)).start();
	}
	
	public static void publishChatMemberUpdatedEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onChatMemberUpdated(update)).start();
	}
	
	public static void publishChatJoinRequestEvent (@Nonnull Update update) {
		new EventPublisher(update, x -> x.onChatJoinRequest(update)).start();
	}
	
}
