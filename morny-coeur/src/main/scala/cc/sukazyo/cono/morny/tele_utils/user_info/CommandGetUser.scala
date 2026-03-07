package cc.sukazyo.cono.morny.tele_utils.user_info

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.system.telegram_api.Standardize
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.execute
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ITelegramCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramUserInformation
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import cc.sukazyo.cono.morny.util.SttpPublic
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.GetChatMember

class CommandGetUser (using coeur: MornyCoeur) extends ITelegramCommand {
	import coeur.dsl.given
	
	override val name: String = "user"
	override val aliases: List[ICommandAlias] = Nil
	override val paramRule: String = "[userid]"
	override val description: String = "获取指定或回复的用户相关信息"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		val args = command.args
		val ccMsg = Messages.derive(event.message)
		
		if (args.length > 1)
			ccMsg("[Unavailable] Too much arguments.").send
			return
		
		val userId: Long =
			if (args nonEmpty) {
				try args(0) toLong
				catch case e: NumberFormatException =>
					ccMsg(s"[Unavailable] ${e.getMessage}").send
					return
			} else if (event.message.replyToMessage eq null) event.message.from.id
			else event.message.replyToMessage.from.id
		
		val response = GetChatMember(event.message.chat.id, userId).execute
		
		if (response.chatMember eq null) {
			ccMsg("[Unavailable] user not found.").send
			return
		}
		
		val user = response.chatMember.user
		
		if (user.id == Standardize.CHANNEL_SPEAKER_MAGIC_ID)
			ccMsg("<code>$__channel_identify</code>").send
			return;
		
		ccMsg(Texts.html(
			TelegramUserInformation.getFormattedInformation(user)(using SttpPublic.mornyBasicRequest)
		)).send
		
	}
	
}
