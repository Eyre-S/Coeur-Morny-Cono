package cc.sukazyo.cono.morny.social_share.command

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ITelegramCommand}
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.social_share.event.OnGetSocial
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendSticker
import com.pengrad.telegrambot.TelegramBot

class GetSocial (using coeur: MornyCoeur) extends ITelegramCommand {
	private given TelegramBot = coeur.account
	
	override val name: String = "get"
	override val aliases: List[ICommandAlias] = Nil
	override val paramRule: String = "<tweet-url|weibo-status-url>"
	override val description: String = "从社交媒体分享链接获取其内容"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		def do404 (): Unit =
			SendSticker(
				event.message.chat.id,
				TelegramStickers.ID_404
			).replyToMessageId(event.message.messageId())
				.unsafeExecute
		
		val content =
			if command.args.length > 0 then
				Right(command.args(0))
			else if event.message.replyToMessage != null then
				import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Message.textWithUrls
				Left(event.message.replyToMessage.textWithUrls)
			else
				do404()
				return
		
		if !OnGetSocial.tryFetchSocial(content)(using event.message.chat.id, event.message.messageId) then
			do404()
		
	}
	
}
