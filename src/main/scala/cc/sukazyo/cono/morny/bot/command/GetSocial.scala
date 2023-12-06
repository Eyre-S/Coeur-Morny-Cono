package cc.sukazyo.cono.morny.bot.command
import cc.sukazyo.cono.morny.data.TelegramStickers
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.bot.event.OnGetSocial
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendSticker

class GetSocial (using coeur: MornyCoeur) extends ITelegramCommand {
	
	override val name: String = "get"
	override val aliases: Array[ICommandAlias] | Null = null
	override val paramRule: String = "<tweet-url|weibo-status-url>"
	override val description: String = "从社交媒体分享链接获取其内容"
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		def do404 (): Unit =
			coeur.account exec SendSticker(
				event.message.chat.id,
				TelegramStickers.ID_404
			).replyToMessageId(event.message.messageId())
		
		if command.args.length < 1 then { do404(); return }
		
		if !OnGetSocial.tryFetchSocial(Right(command.args(0)))(using event.message.chat.id, event.message.messageId) then
			do404()
		
	}
	
}
