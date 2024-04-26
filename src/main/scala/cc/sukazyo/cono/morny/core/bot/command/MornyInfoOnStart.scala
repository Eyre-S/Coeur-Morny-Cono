package cc.sukazyo.cono.morny.core.bot.command

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.{ICommandAlias, ISimpleCommand}
import cc.sukazyo.cono.morny.data.MornyInformation.{getAboutPic, getMornyAboutLinksVars}
import cc.sukazyo.cono.morny.util.tgapi.InputCommand
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.unsafeExecute
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendPhoto

class MornyInfoOnStart (using coeur: MornyCoeur) extends ISimpleCommand {
	import coeur.dsl.{*, given}
	
	override val name: String = "start"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		
		given lang: String = event.message.from.prefer_language
		SendPhoto(
			event.message.chat.id,
			getAboutPic
		).caption(
			translations.trans(
				"morny.command.info.sub_start.message",
				translations.transAsVar("morny.information.about_links", getMornyAboutLinksVars*)
			).stripMargin
		).parseMode(ParseMode HTML)
			.unsafeExecute
		
	}
	
}
