package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.data.MornyInformation.{getAboutPic, getMornyAboutLinksHTML}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendPhoto

import scala.language.postfixOps

class MornyInfoOnStart (using coeur: MornyCoeur) extends ISimpleCommand {
	
	override val name: String = "start"
	override val aliases: Array[ICommandAlias] | Null = null
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		coeur.extra exec new SendPhoto(
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
		
	}
	
}
