package cc.sukazyo.cono.morny.bot.event;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.MessageEntity;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendSticker;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.bot.api.EventListener;
import cc.sukazyo.cono.morny.data.TelegramStickers;

public class OnCallMsgSend extends EventListener {
	
	private static final Pattern REGEX_MSG_SENDREQ_DATA_HEAD = Pattern.compile("^*msg*\n$");
	
	private static record MessageToSend (
			String message,
			MessageEntity[] entities,
			ParseMode parseMode,
			long targetId
	) { }
	
	@Override
	public boolean onMessage(Update update) {
		
		if (update.message().chat().type() != Chat.Type.Private) return false;
		if (update.message().text() == null) return false;
		if (!update.message().text().startsWith("*msg")) return false;
		
		if (!MornyCoeur.trustedInstance().isTrusted(update.message().from().id())) {
			MornyCoeur.extra().exec(new SendSticker(
					update.message().chat().id(),
					TelegramStickers.ID_403
			));
			return true;
		}
		
		// *msg 检查标识
		if (update.message().text().equals("*msg")) {
			if (update.message().replyToMessage() == null) MornyCoeur.extra().exec(new SendSticker(
					update.message().chat().id(),
					TelegramStickers.ID_404
			));
			return true;
		}
		if (update.message().text().equals("*msgsend")) {
			if (update.message().replyToMessage() == null) MornyCoeur.extra().exec(new SendSticker(
					update.message().chat().id(),
					TelegramStickers.ID_404
			));
			return true;
		}
		
		return true;
		
	}
	
	@Nullable
	private static MessageToSend parseRequest (Message requestBody) {
		
		final Matcher matcher = REGEX_MSG_SENDREQ_DATA_HEAD.matcher(requestBody.text());
		if (matcher.matches()) {
			long targetId = Long.parseLong(matcher.group(1));
			final ParseMode parseMode = switch (matcher.group(2)) {
				case "markdown", "md", "m↓" -> ParseMode.MarkdownV2;
				case "md1" -> ParseMode.MarkdownV2;
				case "html" -> ParseMode.HTML;
				default -> null;
			};
			final int offset = 2;
			final ArrayList<MessageEntity> entities = new ArrayList<>();
			for (MessageEntity entity : requestBody.entities()) {
				final MessageEntity parsed = new MessageEntity(entity.type(), entity.offset() - offset, entity.length());
				if (entity.url() != null) parsed.url(entity.url());
				if (entity.user() != null) parsed.user(entity.user());
				if (entity.language() != null) parsed.language(entity.language());
				entities.add(parsed);
			}
			return new MessageToSend(matcher.group(3), null, parseMode, targetId);
		}
		
		return null;
		
	}
	
}
