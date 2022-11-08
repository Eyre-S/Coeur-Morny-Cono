package cc.sukazyo.cono.morny.bot.event;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetChat;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.data.TelegramStickers;
import com.pengrad.telegrambot.response.GetChatResponse;
import com.pengrad.telegrambot.response.SendResponse;

import static cc.sukazyo.cono.morny.util.tgapi.formatting.MsgEscape.escapeHtml;


public class OnCallMsgSend extends EventListener {
	
	private static final Pattern REGEX_MSG_SENDREQ_DATA_HEAD = Pattern.compile("^\\*msg([\\d-]+)(\\*\\S+)?\\n([\\s\\S]+)$");
	
	private record MessageToSend (
			String message,
			MessageEntity[] entities,
			ParseMode parseMode,
			long targetId
	) { }
	
	@Override
	public boolean onMessage(@Nonnull Update update) {
		
		// 执行体检查
		if (update.message().chat().type() != Chat.Type.Private) return false;
		if (update.message().text() == null) return false;
		if (!update.message().text().startsWith("*msg")) return false;
		
		// 权限检查
		if (!MornyCoeur.trustedInstance().isTrusted(update.message().from().id())) {
			MornyCoeur.extra().exec(new SendSticker(
					update.message().chat().id(),
					TelegramStickers.ID_403
			).replyToMessageId(update.message().messageId()));
			return true;
		}
		
		Message msgsendReqRaw; // 用户书写的发送请求原文
		MessageToSend msgsendReqBody; // 解析后的发送请求实例
		
		// *msgsend 发送标识
		// 处理发送要求
		if (update.message().text().equals("*msgsend")) {
			// 发送体处理
			if (update.message().replyToMessage() == null) return answer404(update);
			msgsendReqBody = parseRequest(update.message().replyToMessage());
			if (msgsendReqBody == null) return answer404(update);
			// 执行发送任务
			SendResponse sendResponse = MornyCoeur.getAccount().execute(parseMessageToSend(msgsendReqBody));
			if (!sendResponse.isOk()) { // 发送失败
				MornyCoeur.extra().exec(new SendMessage(
						update.message().chat().id(),
						String.format("""
							<b><u>%d</u> FAILED</b>
							<code>%s</code>""",
								sendResponse.errorCode(),
								sendResponse.description()
						)
				).replyToMessageId(update.message().messageId()).parseMode(ParseMode.HTML));
			} else { // 发送成功信号
				MornyCoeur.extra().exec(new SendSticker(
						update.message().chat().id(),
						TelegramStickers.ID_SENT
				).replyToMessageId(update.message().messageId()));
			}
			return true;
			// 发送完成/失败 - 事件结束
		}
		
		// *msg 检查标识
		if (update.message().text().equals("*msg")) { // 处理对曾经的原文的检查
			if (update.message().replyToMessage() == null) {
				return answer404(update);
			}
			msgsendReqRaw = update.message().replyToMessage();
		} else if (update.message().text().startsWith("*msg")) { // 对接受到的原文进行检查
			msgsendReqRaw = update.message();
		} else {
			return answer404(update); // 未定义的动作
		}
		
		// 对发送请求的用户原文进行解析
		msgsendReqBody = parseRequest(msgsendReqRaw);
		if (msgsendReqBody == null) {
			return answer404(update);
		}
		
		// 输出发送目标信息
		GetChatResponse targetChatReq = MornyCoeur.getAccount().execute(new GetChat(msgsendReqBody.targetId()));
		if (!targetChatReq.isOk()) {
			MornyCoeur.extra().exec(new SendMessage(
					update.message().chat().id(),
					String.format("""
							<b><u>%d</u> FAILED</b>
							<code>%s</code>""",
							targetChatReq.errorCode(),
							targetChatReq.description()
					)
			).replyToMessageId(update.message().messageId()).parseMode(ParseMode.HTML));
		} else {
			MornyCoeur.extra().exec(new SendMessage(
					update.message().chat().id(),
					targetChatReq.chat().type() == Chat.Type.Private ? (
							String.format("""
									<i><u>%d</u>@%s</i>
									🔒 <b>%s</b> %s""",
									msgsendReqBody.targetId(),
									escapeHtml(targetChatReq.chat().type().name()),
									escapeHtml(targetChatReq.chat().firstName()+(targetChatReq.chat().lastName()==null?"":" "+targetChatReq.chat().lastName())),
									targetChatReq.chat().username()==null?
											String.format("<a href='tg://user?id=%d'>@@</a>", targetChatReq.chat().id()):
											(escapeHtml("@"+targetChatReq.chat().username()))
							)
					) : (
							String.format("""
									<i><u>%d</u>@%s</i>:::
									%s <b>%s</b>%s""",
									msgsendReqBody.targetId(),
									escapeHtml(targetChatReq.chat().type().name()),
									switch (targetChatReq.chat().type()) {
										case group -> "💭";
										case channel -> "📢";
										case supergroup -> "💬";
										default -> "⭕️";
									},
									escapeHtml(targetChatReq.chat().title()),
									targetChatReq.chat().username() != null?String.format(
											"  @%s", escapeHtml(targetChatReq.chat().username())
									):""
							)
					)
			).replyToMessageId(update.message().messageId()).parseMode(ParseMode.HTML));
		}
		// 发送文本测试
		SendResponse testSendResp = MornyCoeur.getAccount().execute(
				parseMessageToSend(msgsendReqBody, update.message().chat().id()).replyToMessageId(update.message().messageId())
		);
		if (!testSendResp.isOk()) {
			MornyCoeur.extra().exec(new SendMessage(
					update.message().chat().id(),
					String.format("""
							<b><u>%d</u> FAILED</b>
							<code>%s</code>""",
							testSendResp.errorCode(),
							testSendResp.description()
					)
			).replyToMessageId(update.message().messageId()).parseMode(ParseMode.HTML));
		}
		
		return true;
		
	}
	
	@Nullable
	private static MessageToSend parseRequest (@Nonnull Message requestBody) {
		
		final Matcher matcher = REGEX_MSG_SENDREQ_DATA_HEAD.matcher(requestBody.text());
		if (matcher.matches()) {
			long targetId = Long.parseLong(matcher.group(1));
			 ParseMode parseMode = matcher.group(2) == null ? null : switch (matcher.group(2)) {
				case "*markdown", "*md", "*m↓" -> ParseMode.MarkdownV2;
				case "*md1" -> ParseMode.Markdown;
				case "*html" -> ParseMode.HTML;
				default -> null;
			};
			final int offset = "*msg".length()+matcher.group(1).length()+(matcher.group(2)==null?0:matcher.group(2).length())+1;
			final ArrayList<MessageEntity> entities = new ArrayList<>();
			if (requestBody.entities() != null) for (MessageEntity entity : requestBody.entities()) {
				final MessageEntity parsed = new MessageEntity(entity.type(), entity.offset() - offset, entity.length());
				if (entity.url() != null) parsed.url(entity.url());
				if (entity.user() != null) parsed.user(entity.user());
				if (entity.language() != null) parsed.language(entity.language());
				entities.add(parsed);
			}
			return new MessageToSend(matcher.group(3), entities.toArray(MessageEntity[]::new), parseMode, targetId);
		}
		
		return null;
		
	}
	
	@Nonnull
	private static SendMessage parseMessageToSend (@Nonnull MessageToSend body) {
		return parseMessageToSend(body, body.targetId);
	}
	
	@Nonnull
	private static SendMessage parseMessageToSend (@Nonnull MessageToSend body, long targetId) {
		SendMessage sendingBody = new SendMessage(targetId, body.message);
		if (body.entities != null) sendingBody.entities(body.entities);
		if (body.parseMode != null) sendingBody.parseMode(body.parseMode);
		return sendingBody;
	}
	
	private static boolean answer404 (@Nonnull Update update) {
		MornyCoeur.extra().exec(new SendSticker(
				update.message().chat().id(),
				TelegramStickers.ID_404
		).replyToMessageId(update.message().messageId()));
		return true;
	}
	
}
