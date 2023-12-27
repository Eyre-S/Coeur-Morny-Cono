package cc.sukazyo.cono.morny.reporter

import cc.sukazyo.cono.morny.core.{MornyCoeur, MornyConfig}
import cc.sukazyo.cono.morny.core.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.core.bot.api.{EventEnv, EventListener}
import cc.sukazyo.cono.morny.data.MornyInformation.getVersionAllFullTagHTML
import cc.sukazyo.cono.morny.util.statistics.NumericStatistics
import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.util.EpochDateTime.DurationMillis
import cc.sukazyo.cono.morny.util.schedule.CronTask
import com.cronutils.builder.CronBuilder
import com.cronutils.model.Cron
import com.cronutils.model.definition.CronDefinitionBuilder
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.request.{BaseRequest, SendMessage}
import com.pengrad.telegrambot.response.BaseResponse
import com.pengrad.telegrambot.TelegramException

import java.time.ZoneId

class MornyReport (using coeur: MornyCoeur) {
	
	private val enabled = coeur.config.reportToChat != -1
	if !enabled then
		logger info "Morny Report is disabled : report chat is set to -1"
	
	private def executeReport[T <: BaseRequest[T, R], R<: BaseResponse] (report: T): Unit = {
		if !enabled then return;
		try {
			coeur.account exec report
		} catch case e: EventRuntimeException => {
			import EventRuntimeException.*
			e match
				case e: ActionFailed =>
					logger warn
						s"""cannot execute report to telegram:
						   |${exceptionLog(e) indent 4}
						   |  tg-api response:
						   |${(e.response toString) indent 4}""".stripMargin
				case e: ClientFailed =>
					logger error
						s"""failed when report to telegram:
						   |${exceptionLog(e.getCause) indent 4}
						   |""".stripMargin
		}
	}
	
	def exception (e: Throwable, description: String|Null = null): Unit = {
		def _tgErrFormat: String = e match
			case api: EventRuntimeException.ActionFailed =>
				// language=html
				"\n\ntg-api error:\n<pre><code class='language-json'>%s</code></pre>"
						.formatted(GsonBuilder().setPrettyPrinting().create.toJson(api.response))
			case tgErr: TelegramException if tgErr.response != null =>
				// language=html
				"\n\ntg-api error:\n<pre><code class='language-json'>%s</code></pre>"
					.formatted(GsonBuilder().setPrettyPrinting().create.toJson(tgErr.response))
			case _ => ""
		executeReport(SendMessage(
			coeur.config.reportToChat,
			// language=html
			s"""<b>▌Coeur Unexpected Exception </b>
			   |${if description ne null then h(description)+"\n" else ""}
			   |<pre><code class="language-log">${h(exceptionLog(e))}</code></pre>$_tgErrFormat"""
			.stripMargin
		).parseMode(ParseMode HTML))
	}
	
	def unauthenticatedAction (action: String, user: User): Unit = {
		executeReport(SendMessage(
			coeur.config.reportToChat,
			// language=html
			s"""<b>▌User unauthenticated action</b>
			   |action: ${h(action)}
			   |by user ${user.fullnameRefHTML}"""
			.stripMargin
		).parseMode(ParseMode HTML))
	}
	
	def reportCoeurMornyLogin(): Unit = {
		executeReport(SendMessage(
			coeur.config.reportToChat,
			// language=html
			s"""<b>▌Morny Logged in</b>
			   |-v $getVersionAllFullTagHTML
			   |Logged into user: @${coeur.username}
			   |
			   |as config fields:
			   |${sectionConfigFields(coeur.config)}
			   |
			   |Report Daemon will use TimeZone <code>${coeur.config.reportZone.getDisplayName}</code> for following report."""
			.stripMargin
		).parseMode(ParseMode HTML))
	}
	
	private def sectionConfigFields (config: MornyConfig): String = {
		val echo = StringBuilder()
		for (field <- config.getClass.getFields) {
			// language=html
			echo ++= s"- <i><u>${field.getName}</u></i> "
			try {
				if (field.isAnnotationPresent(classOf[MornyConfig.Sensitive])) {
					echo ++= /*language=html*/ ": <i>sensitive_field</i>"
				} else {
					val value = field.get(config)
					// language=html
					echo ++= "= " ++= (if value eq null then "<i>null</i>" else s"<code>${h(value.toString)}</code>")
				}
			
			} catch
				// noinspection ScalaUnnecessaryParentheses
				case e: (IllegalAccessException|IllegalArgumentException|NullPointerException) =>
					// language=html
					echo ++= s": <i>${h("<read-error>")}</i>"
					logger error
							s"""error while reading config field ${field.getName}
							   |${exceptionLog(e)}""".stripMargin
					exception(e, s"error while reading config field ${field.getName}")
			echo ++= "\n"
		}
		echo dropRight 1 toString
	}
	
	def reportCoeurExit (): Unit = {
		def _causedTag = coeur.exitReason match
			case Some(_exitReason) => _exitReason match
				case u: User => u.fullnameRefHTML
				case a => /*language=html*/ s"<code>${h(a.toString)}</code>"
			case None => "UNKNOWN reason"
		executeReport(SendMessage(
			coeur.config.reportToChat,
			// language=html
			s"""<b>▌Morny Exited</b>
			   |from user @${coeur.username}
			   |
			   |by: $_causedTag"""
			.stripMargin
		).parseMode(ParseMode HTML))
	}
	
	object EventStatistics {
		
		private var eventTotal = 0
		private var eventCanceled = 0
		private val runningTime: NumericStatistics[DurationMillis] = NumericStatistics()
		
		def reset (): Unit = {
			eventTotal = 0
			eventCanceled = 0
			runningTime.reset()
		}
		
		private def runningTimeStatisticsHTML: String =
			runningTime.value match
				// language=html
				case None => "<i><u>&lt;no-statistics&gt;</u></i>"
				case Some(value) =>
					import cc.sukazyo.cono.morny.util.CommonFormat.formatDuration as f
					s""" - <i>average</i>: <code>${f(value.total / value.count)}</code>
					   | - <i>max time</i>: <code>${f(value.max)}</code>
					   | - <i>min time</i>: <code>${f(value.min)}</code>
					   | - <i>total</i>: <code>${f(value.total)}</code>""".stripMargin
		
		def eventStatisticsHTML: String =
			import cc.sukazyo.cono.morny.util.UseMath.percentageOf as p
			val processed = runningTime.count
			val canceled = eventCanceled
			val ignored = eventTotal - processed - canceled
			// language=html
			s""" - <i>total event received</i>: <code>$eventTotal</code>
			   | - <i>event ignored</i>: (<code>${eventTotal p ignored}%</code>) <code>$ignored</code>
			   | - <i>event canceled</i>: (<code>${eventTotal p canceled}%</code>) <code>$canceled</code>
			   | - <i>event processed</i>: (<code>${eventTotal p processed}%</code>) <code>$processed</code>
			   | - <i>processed time usage</i>:
			   |${runningTimeStatisticsHTML.indent(3)}""".stripMargin
		
		object EventInfoCatcher extends EventListener {
			override def executeFilter (using EventEnv): Boolean = true
			//noinspection ScalaWeakerAccess
			case class EventTimeUsed (it: DurationMillis)
			override def atEventPost (using event: EventEnv): Unit = {
				import event.*
				eventTotal += 1
				event.state match
					case State.OK(from) =>
						val timeUsed = EventTimeUsed(System.currentTimeMillis - event.timeStartup)
						givenCxt << timeUsed
						logger debug
							s"""event done with OK
							   |  with time consumed ${timeUsed.it}ms
							   |  by $from""".stripMargin
						runningTime ++ timeUsed.it
					case State.CANCELED(from) =>
						eventCanceled += 1
						logger debug
							s"""event done with CANCELED"
							   |  by $from""".stripMargin
					case null =>
			}
		}
		
	}
	
	private object DailyReportTask extends CronTask {
		
		import com.cronutils.model.field.expression.FieldExpressionFactory.*
		
		override val name: String = "reporter#event"
		override val cron: Cron = CronBuilder.cron(
			CronDefinitionBuilder.defineCron
				.withHours.and
				.instance
		).withHour(on(0)).instance
		override val zone: ZoneId = coeur.config.reportZone.toZoneId
		
		//noinspection TypeAnnotation
		override def main = {
			
			executeReport(SendMessage(
				coeur.config.reportToChat,
				// language=html
				s"""▌Morny Daily Report
				   |
				   |<b>Event Statistics :</b>
				   |${EventStatistics.eventStatisticsHTML}""".stripMargin
			).parseMode(ParseMode.HTML))
			
			// daily reset
			EventStatistics.reset()
			
		}
		
	}
	
	def start (): Unit = {
		coeur.tasks ++ DailyReportTask
	}
	
	def stop (): Unit = {
		coeur.tasks % DailyReportTask
	}
	
}
