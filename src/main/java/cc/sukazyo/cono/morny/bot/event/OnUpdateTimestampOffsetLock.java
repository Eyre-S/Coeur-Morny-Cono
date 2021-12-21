package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

/**
 * 阻止 {@link MornyCoeur#latestEventTimestamp 指定时间} 之前的事件处理.
 * <p>
 * 只支持以下事件
 * <ul>
 *     <li>{@link EventListener#onMessage(Update) 收到消息}</li>
 *     <li>{@link EventListener#onEditedMessage(Update) 消息被更新}</li>
 *     <li>{@link EventListener#onChannelPost(Update) 收到频道消息}</li>
 *     <li>{@link EventListener#onEditedChannelPost(Update) 频道消息被更新}</li>
 * </ul>
 */
public class OnUpdateTimestampOffsetLock extends EventListener {
	
	@Override
	public boolean onMessage (@NotNull Update update) {
		return update.message().date() < MornyCoeur.latestEventTimestamp/1000;
	}
	
	/** @since 0.4.2.6 */
	@Override
	public boolean onEditedMessage (@Nonnull Update update) {
		return update.editedMessage().editDate() < MornyCoeur.latestEventTimestamp/1000;
	}
	
	/** @since 0.4.2.6 */
	@Override
	public boolean onChannelPost (@Nonnull Update update) {
		return update.channelPost().date() < MornyCoeur.latestEventTimestamp/1000;
	}
	
	/** @since 0.4.2.6 */
	@Override
	public boolean onEditedChannelPost (@Nonnull Update update) {
		return update.editedChannelPost().editDate() < MornyCoeur.latestEventTimestamp/1000;
	}
	
}
