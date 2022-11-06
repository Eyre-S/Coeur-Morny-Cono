package cc.sukazyo.cono.morny.bot.event;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import cc.sukazyo.cono.morny.util.CommonFormat;
import cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape;
import cc.sukazyo.cono.morny.util.tgapi.formatting.TGToString;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;
import com.pengrad.telegrambot.response.SendResponse;

import javax.annotation.Nonnull;

/**
 * 通过 bot 呼叫主人的事件监听管理类
 * @since 0.4.2.1
 */
public class OnCallMe extends EventListener {
	
	/**
	 * 主人的 telegram user id，同时被用于 chat id<br>
	 * 跟随 {@link cc.sukazyo.cono.morny.MornyConfig#trustedMaster} 的值
	 * @since 0.4.2.1
	 */
	private static final long ME = MornyCoeur.config().trustedMaster;
	
	/**
	 * 监听私聊 bot 的消息进行呼叫关键字匹配。
	 * 如果成功，将会执行呼叫函数，并向呼叫者回显{@link TelegramStickers#ID_WAITING "已呼叫"贴纸}
	 *
	 * @param update 事件基础参数，消息事件所属的 tgapi:update 对象
	 * @return 事件基础返回值，是否已完成处理事件：<br>
	 *         如果匹配到呼叫，则返回{@code true}，反之返回{@code false}
	 */
	@Override
	public boolean onMessage (@Nonnull Update update) {
		if (update.message().text() == null)
			return false;
		if (update.message().chat().type() != Chat.Type.Private)
			return false;
		switch (update.message().text().toLowerCase()) {
			case "steam", "sbeam", "sdeam" ->
					requestSteamJoin(update);
			case "hana paresu", "花宫", "内群" ->
					requestHanaParesuJoin(update);
			case "dinner", "lunch", "breakfast", "meal", "eating", "安妮今天吃什么" ->
					requestLastDinner(update);
			default -> {
				if (update.message().text().startsWith("cc::")) {
					requestCustomCall(update);
					break;
				}
				return false;
			}
		}
		MornyCoeur.extra().exec(new SendSticker(
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
		MornyCoeur.extra().exec(new SendMessage(
				ME, String.format(
						"""
						request <b>STEAM LIBRARY</b>
						from %s""",
						TGToString.as(event.message().from()).fullnameRefHtml()
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
		MornyCoeur.extra().exec(new SendMessage(
				ME, String.format(
						"""
						request <b>Hana Paresu</b>
						from %s""",
						TGToString.as(event.message().from()).fullnameRefHtml()
				)
		).parseMode(ParseMode.HTML));
	}
	
	/**
	 * 对访问最近一次的饭局的请求进行回复<br>
	 *
	 * @param event 执行呼叫的tg事件
	 */
	private static void requestLastDinner (Update event) {
		boolean isAllowed = false;
		Message lastDinnerData = null;
		if (MornyCoeur.trustedInstance().isTrustedForDinnerRead(event.message().from().id())) {
			lastDinnerData = MornyCoeur.extra().exec(new GetChat(MornyCoeur.config().dinnerChatId)).chat().pinnedMessage();
			SendResponse sendResp = MornyCoeur.extra().exec(new ForwardMessage(
					event.message().from().id(),
					lastDinnerData.forwardFromChat().id(),
					lastDinnerData.forwardFromMessageId()
			));
			MornyCoeur.extra().exec(new SendMessage(
					event.message().from().id(),
					String.format("<i>on</i> <code>%s [UTC+8]</code>\n- <code>%s</code> <i>before</i>",
							MsgEscape.escapeHtml(
									CommonFormat.formatDate((long)lastDinnerData.forwardDate()*1000, 8)
							), MsgEscape.escapeHtml(
									CommonFormat.formatDuration(System.currentTimeMillis()-(long)lastDinnerData.forwardDate()*1000)
							)
					)
			).replyToMessageId(sendResp.message().messageId()).parseMode(ParseMode.HTML));
			isAllowed = true;
		} else {
			MornyCoeur.extra().exec(new SendSticker(
					event.message().from().id(),
					TelegramStickers.ID_403
			).replyToMessageId(event.message().messageId()));
		}
		MornyCoeur.extra().exec(new SendMessage(
				ME, String.format(
						"""
						request <b>Last Annie Dinner</b>
						from %s
						%s""",
						TGToString.as(event.message().from()).fullnameRefHtml(),
						isAllowed ? "Allowed and returned " + String.format(
								"https://t.me/c/%d/%d", Math.abs(lastDinnerData.forwardFromChat().id()+1000000000000L), lastDinnerData.forwardFromMessageId()
						) : "Forbidden by perm check."
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
		MornyCoeur.extra().exec(new SendMessage(
				ME, String.format(
						"""
						request <u>[???]</u>
						from %s""",
						TGToString.as(event.message().from()).fullnameRefHtml()
				)
		).parseMode(ParseMode.HTML));
		MornyCoeur.extra().exec(new ForwardMessage(
				ME,
				event.message().chat().id(),
				event.message().messageId()
		));
	}
	
}
