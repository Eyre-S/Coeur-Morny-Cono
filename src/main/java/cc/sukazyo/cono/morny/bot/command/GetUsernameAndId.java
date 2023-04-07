package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.cono.morny.util.tgapi.InputCommand;
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramUserInformation;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetChatMember;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetChatMemberResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GetUsernameAndId implements ITelegramCommand {
	
	@Nonnull @Override public String getName () { return "user"; }
	@Nullable @Override public String[] getAliases () { return null; }
	@Nonnull @Override public String getParamRule () { return "[userid]"; }
	@Nonnull @Override public String getDescription () { return "获取指定或回复的用户相关信息"; }
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		final String[] args = command.getArgs();
		
		// 不支持大于一个参数
		if (args.length > 1) { MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				"[Unavailable] Too much arguments."
		).replyToMessageId(event.message().messageId())); return; }
		
		// 发送者自己的 id
		long userId = event.message().from().id();
		
		// 如果有回复某个人，则使用被回复人的 id
		if (event.message().replyToMessage()!= null) {
			userId = event.message().replyToMessage().from().id();
		}
		// 如果有指定 id，则使用指定的 id
		if (args.length > 0) {
			try {
				userId = Long.parseLong(args[0]);
			} catch (NumberFormatException e) {
				MornyCoeur.extra().exec(new SendMessage(
						event.message().chat().id(),
						"[Unavailable] " + e.getMessage()
				).replyToMessageId(event.message().messageId()));
				return;
			}
		}
		
		// 重新获取用户对象
		final GetChatMemberResponse response = MornyCoeur.getAccount().execute(
				new GetChatMember(event.message().chat().id(), userId)
		);
		
		if (response.chatMember() == null) {
			MornyCoeur.extra().exec(new SendMessage(
					event.message().chat().id(),
					"[Unavailable] user not found."
			).replyToMessageId(event.message().messageId()));
			return;
		}
		
		// 获取并发送用户信息
		final User user = response.chatMember().user();
		
		if (user.id() == 136817688) {
			MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				"<code>$__channel_identify</code>"
			));
			return;
		}

		MornyCoeur.extra().exec(new SendMessage(
				event.message().chat().id(),
				TelegramUserInformation.informationOutputHTML(user)
		).replyToMessageId(event.message().messageId()).parseMode(ParseMode.HTML));
		
	}
	
}
