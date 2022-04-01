package cc.sukazyo.cono.morny.bot.command;

import cc.sukazyo.cono.morny.MornyCoeur;
import cc.sukazyo.untitled.util.telegram.object.InputCommand;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.GetChatMember;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DirectMsgClear implements ISimpleCommand {
	
	@Nonnull @Override public String getName () { return "/r"; }
	
	@Nullable @Override public String[] getAliases () { return new String[0]; }
	
	@Override
	public void execute (@Nonnull InputCommand command, @Nonnull Update event) {
		
		if (event.message().replyToMessage() == null) return;
		if (event.message().replyToMessage().from().id() != MornyCoeur.getUserid()) return;
		if (event.message().replyToMessage().date() - System.currentTimeMillis()/1000 < 48*60*60) return;
		
		final boolean isTrusted = MornyCoeur.trustedInstance().isTrusted(event.message().chat().id());
		
		if (
				isTrusted || (
						event.message().replyToMessage().replyToMessage() != null &&
						event.message().replyToMessage().replyToMessage().from().id().equals(event.message().from().id())
				)
		) {
			
			MornyCoeur.extra().exec(new DeleteMessage(
					event.message().chat().id(), event.message().replyToMessage().messageId()
			));
			if (event.message().chat().type() == Chat.Type.Private || (
					MornyCoeur.extra().exec(
							new GetChatMember(event.message().chat().id(), event.message().from().id())
					).chatMember().canDeleteMessages()
			)) {
				MornyCoeur.extra().exec(new DeleteMessage(
						event.message().chat().id(), event.message().messageId()
				));
			}
			
		}
		
	}
	
}
