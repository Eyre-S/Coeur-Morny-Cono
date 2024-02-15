package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.data.MornyInformation.{getAboutPic, getMornyAboutLinksHTML}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendPhoto
import com.pengrad.telegrambot.TelegramBot

class MornyInfoOnStart (using coeur: MornyCoeur) extends ISimpleCommand {
	private given TelegramBot = coeur.account
	
	override val name: String = "start"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		SendPhoto(
			event.message.chat.id,
			getAboutPic
		).caption(
			s"""欢迎使用 <b>Morny Cono</b>，<i>来自安妮的侍从小鼠</i>。
			   |Morny 具有各种各样的功能。
			   |
			   |————————————————
			   |$getMornyAboutLinksHTML
			   |————————————————
			   |
			   |（你可以随时通过 /info 重新获得这些信息）"""
			.stripMargin
		).parseMode(ParseMode HTML)
			.unsafeExecute
		
	}
	
}
