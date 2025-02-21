package cc.sukazyo.cono.morny.reporter.command

import cc.sukazyo.cono.morny.reporter.MornyReport
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, InputCommand, ISimpleCommand}
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.util.var_text.VarText
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.SendMessage

class Info_EventStatistic (using reporter: MornyReport) extends ISimpleCommand {
	import reporter.coeur.dsl.given
	
	override val name: String = "event"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		SendMessage(
			event.message.chat.id,
			VarText(
				// language=html
				"""<b>Event Statistics :</b>
				  |in today
				  |{event_statistics}""".stripMargin
			).render(
				"event_statistics" -> reporter.EventStatistics.eventStatisticsHTML
			)
		).parseMode(ParseMode.HTML).replyToMessageId(event.message.messageId)
			.unsafeExecute
	}
	
}
