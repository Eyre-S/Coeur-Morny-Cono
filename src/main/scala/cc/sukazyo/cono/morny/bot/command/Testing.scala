package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.extra.BilibiliForms
import cc.sukazyo.cono.morny.extra.bilibili.XWebAPI
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{SendMessage, SendPhoto}

import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h

import scala.language.postfixOps

class Testing (using coeur: MornyCoeur) extends ISimpleCommand {
	
	override val name: String = "test"
	override val aliases: Array[ICommandAlias] | Null = null
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		val video = BilibiliForms.parse_videoUrl(command.args.mkString(" "))
		val video_info = XWebAPI.get_view(video)
		
		coeur.account exec new SendPhoto(
			event.message.chat.id,
			video_info.data.pic
		).replyToMessageId(event.message.messageId)
			.caption(
				// language=html
				s"""<a href="https://www.bilibili.com/video/av${video.av}"><b>${h(video_info.data.title)}</b></a>
				   |  <a href="https://space.bilibili.com/${video_info.data.owner.mid}">@${h(video_info.data.owner.name)}</a>
				   |${h(video_info.data.desc)}""".stripMargin
			).parseMode(ParseMode.HTML)
		
		coeur.account exec new SendMessage(
			event.message.chat.id,
			// language=html
			"<b>Just</b> a TEST command."
		).replyToMessageId(event.message.messageId).parseMode(ParseMode HTML)
		
	}
	
}
