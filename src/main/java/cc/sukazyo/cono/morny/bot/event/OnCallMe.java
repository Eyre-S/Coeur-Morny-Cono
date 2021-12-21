package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.MornyTrusted;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import org.jetbrains.annotations.NotNull;

/**
 * 通过 bot 呼叫主人的事件监听管理类
 * @since 0.4.2.1
 */
public class OnCallMe extends EventListener {
	
	/**
	 * 主人的 telegram user id，同时被用于 chat id<br>
	 * 跟随 {@link MornyTrusted#MASTER} 的值
	 * @since 0.4.2.1
	 */
	private static final long ME = MornyCoeur.trustedInstance().MASTER;
	
	/**
	 * 监听私聊 bot 的消息进行呼叫关键字匹配。
	 * 如果成功，将会执行呼叫函数，并向呼叫者回显{@link TelegramStickers#ID_WAITING "已呼叫"贴纸}
	 *
	 * @param update 事件基础参数，消息事件所属的 tgapi:update 对象
	 * @return 事件基础返回值，是否已完成处理事件：<br>
	 *         如果匹配到呼叫，则返回{@code true}，反之返回{@code false}
	 */
	@Override
	public boolean onMessage (@NotNull Update update) {
		if (update.message().text() == null)
			return false;
		if (update.message().chat().type() != Chat.Type.Private)
			return false;
		switch (update.message().text().toLowerCase()) {
			case "steam", "sbeam", "sdeam" ->
					requestSteamJoin(update);
			case "hana paresu", "花宫", "内群" ->
					requestHanaParesuJoin(update);
			default -> {
				if (update.message().text().startsWith("cc::")) {
					requestCustomCall(update);
					break;
				}
				return false;
			}
		}
		MornyCoeur.getAccount().execute(new SendSticker(
						update.message().chat().id(),
						TelegramStickers.ID_SENT
				).replyToMessageId(update.message().messageId())
		);
		return true;
	}
	
	/**
	 * 执行 steam library 呼叫<br>
	 * 将会向 {@link #ME} 发送
	 *
	 * @param event 执行呼叫的tg事件
	 */
	private static void requestSteamJoin (Update event) {
		MornyCoeur.getAccount().execute(new SendMessage(
				ME, String.format("""
						request <b>STEAM LIBRARY</b>
						from <a href="tg://user?id=%d">%s</a>""",
						event.message().from().id(),
						event.message().from().firstName() + " " + event.message().from().lastName()
				)
		).parseMode(ParseMode.HTML));
	}
	
	/**
	 * 执行花宫呼叫<br>
	 * 将会向 {@link #ME} 发送
	 *
	 * @param event 执行呼叫的tg事件
	 */
	private static void requestHanaParesuJoin (Update event) {
		MornyCoeur.getAccount().execute(new SendMessage(
				ME, String.format("""
						request <b>Hana Paresu</b>
						from <a href="tg://user?id=%d">%s</a>""",
						event.message().from().id(),
						event.message().from().firstName() + " " + event.message().from().lastName()
				)
		).parseMode(ParseMode.HTML));
	}
	
	/**
	 * 执行自定义呼叫<br>
	 * 将会向 {@link #ME} 发送一个 request 数据消息和转发的原始请求消息<br>
	 * <br>
	 * <u>known issue</u><ul>
	 *     <li>无法处理与转发带有媒体的消息</li>
	 * </ul>
	 * <br>
	 * 现在你可以通过这个 bot 来呼叫主人（sukazyo）任何事情了 ——
	 * <s>但是直接私聊sukazyo不好吗</s>
	 *
	 * @param event 执行呼叫的tg事件
	 * @since 0.4.2.2
	 */
	private static void requestCustomCall (Update event) {
		MornyCoeur.getAccount().execute(new SendMessage(
				ME, String.format("""
						request <u>[???]</u>
						from <a href="tg://user?id=%d">%s</a>""",
						event.message().from().id(),
						event.message().from().firstName() + " " + event.message().from().lastName()
				)
		).parseMode(ParseMode.HTML));
		MornyCoeur.getAccount().execute(new ForwardMessage(
				ME,
				event.message().chat().id(),
				event.message().messageId()
		));
	}
	
}
