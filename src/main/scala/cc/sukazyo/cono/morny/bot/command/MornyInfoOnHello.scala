package cc.sukazyo.cono.morny.bot.command

import cc.sukazyo.cono.morny.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendPhoto

import scala.language.postfixOps

object MornyInfoOnHello extends ISimpleCommand {
	
	override def getName: String = "start"
	override def getAliases: Array[String] = Array()
	
	override def execute (command: InputCommand, event: Update): Unit = {
		
		MornyCoeur.extra exec new SendPhoto(
			event.message.chat.id,
			MornyInformation.getAboutPic
		).caption(
			s"""欢迎使用 <b>Morny Cono</b>，<i>来自安妮的侍从小鼠</i>。
			   |Morny 具有各种各样的功能。
			   |
			   |————————————————
			   |${MornyInformation.getMornyAboutLinksHTML}
			   |————————————————
			   |
			   |（你可以随时通过 /info 重新获得这些信息）"""
			.stripMargin
		).parseMode(ParseMode HTML)
		
	}
	
}
