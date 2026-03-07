package cc.sukazyo.cono.morny.reporter.command

import cc.sukazyo.cono.morny.reporter.MornyReport
import cc.sukazyo.cono.morny.system.telegram_api.command.{ICommandAlias, ISimpleCommand, InputCommand}
import cc.sukazyo.cono.morny.system.telegram_api.message.Messages
import cc.sukazyo.cono.morny.system.telegram_api.text.Texts
import cc.sukazyo.cono.morny.util.var_text.VarText
import com.pengrad.telegrambot.model.Update

class Info_EventStatistic (using reporter: MornyReport) extends ISimpleCommand {
	import reporter.coeur.dsl.given
	
	override val name: String = "event"
	override val aliases: List[ICommandAlias] = Nil
	
	override def execute (using command: InputCommand, event: Update): Unit = {
		Messages.derive(event.message)(Texts.html(
			VarText(
				// language=html
				"""<b>Event Statistics :</b>
				  |in today
				  |{event_statistics}""".stripMargin
			).render(
				"event_statistics" -> reporter.EventStatistics.eventStatisticsHTML
			)
		)).send
	}
	
}
