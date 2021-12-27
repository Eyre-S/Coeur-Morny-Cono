package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import com.pengrad.telegrambot.model.Update;

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
 * @see #isOutdated 时间判断
 */
public class OnUpdateTimestampOffsetLock extends EventListener {
	
	/**
	 * 检查传入时间是否在要求时间之前（即"过期"）.
	 * @param timestamp 传入时间，秒级
	 * @return 如果传入时间在要求时间<u>之前</u>，返回true，反之false
	 * @since 0.4.2.7
	 */
	public boolean isOutdated(long timestamp) {
		return timestamp < MornyCoeur.getLatestEventTimestamp()/1000;
	}
	
	@Override
	public boolean onMessage (@Nonnull Update update) {
		return isOutdated(update.message().date());
	}
	
	/** @since 0.4.2.6 */
	@Override
	public boolean onEditedMessage (@Nonnull Update update) {
		return isOutdated(update.editedMessage().editDate());
	}
	
	/** @since 0.4.2.6 */
	@Override
	public boolean onChannelPost (@Nonnull Update update) {
		return isOutdated(update.channelPost().date());
	}
	
	/** @since 0.4.2.6 */
	@Override
	public boolean onEditedChannelPost (@Nonnull Update update) {
		return isOutdated(update.editedChannelPost().editDate());
	}
	
}
