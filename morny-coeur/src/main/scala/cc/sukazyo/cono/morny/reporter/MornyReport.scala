package cc.sukazyo.cono.morny.reporter

import cc.sukazyo.cono.morny.core.{MornyCoeur, MornyConfig}
import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.data.MornyInformation.getVersionAllFullTagHTML
import cc.sukazyo.cono.morny.reporter.telegram_bot.{BotErrorsReport, CoreCommandsReports}
import cc.sukazyo.cono.morny.system.telegram_api.event.{EventEnv, EventListener, EventRuntimeException}
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramParseEscape.escapeHtml as h
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Requests.unsafeExecute
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Update.{sourceChat, sourceUser}
import cc.sukazyo.cono.morny.system.utils.CommonEncrypt.hashId
import cc.sukazyo.cono.morny.system.utils.ConvertByteHex.toHex
import cc.sukazyo.cono.morny.system.utils.EpochDateTime.DurationMillis
import cc.sukazyo.cono.morny.util.schedule.CronTask
import cc.sukazyo.cono.morny.util.statistics.{NumericStatistics, UniqueCounter}
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import com.cronutils.builder.CronBuilder
import com.cronutils.model.Cron
import com.cronutils.model.definition.CronDefinitionBuilder
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.{Chat, User}
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.{BaseRequest, SendMessage}
import com.pengrad.telegrambot.response.BaseResponse
import com.pengrad.telegrambot.TelegramException

import java.time.ZoneId

class MornyReport (using val coeur: MornyCoeur) {
	
	given reporter: MornyReport = this
	
	private val enabled = coeur.config.reportToChat != -1
	if !enabled then
		logger `info` "Morny Report is disabled : report chat is set to -1"
	
	private def executeReport[T <: BaseRequest[T, R], R<: BaseResponse] (report: T): Unit = {
		if !enabled then return
		try {
			report.unsafeExecute(using coeur.account)
		} catch case e: EventRuntimeException => {
			import EventRuntimeException.*
			e match
				case e: ActionFailed =>
					logger `warn`
						s"""cannot execute report to telegram:
						   |${e.toLogString `indent` 4}
						   |  tg-api response:
						   |${(e.response toString) `indent` 4}""".stripMargin
				case e: ClientFailed =>
					logger `error`
						s"""failed when report to telegram:
						   |${e.getCause.toLogString `indent` 4}
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
			   |<pre><code class="language-log">${h(e.toLogString)}</code></pre>$_tgErrFormat"""
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
					logger `error`
							s"""error while reading config field ${field.getName}
							   |${e.toLogString}""".stripMargin
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
	
	object botErrorsReport extends BotErrorsReport()
	object coreCommandsReports extends CoreCommandsReports()
	
	object EventStatistics {
		
		private var eventTotal = 0
		private var eventCanceled = 0
		private val runningTime: NumericStatistics[DurationMillis] = NumericStatistics()
		/** The event which is from a private chat (mostly message) */
		private val event_from_private = UniqueCounter[String]()
		/** The event which is from a group (message, or member join etc.) */
		private val event_from_group = UniqueCounter[String]()
		/** The event which is from a channel (message, or member join etc.) */
		private val event_from_channel = UniqueCounter[String]()
		/** The event which is from a user's action (inline queries etc. which have a executor but not belongs to a chat.) */
		private val event_from_user_action = UniqueCounter[String]()
		
		def reset (): Unit = {
			eventTotal = 0
			eventCanceled = 0
			runningTime.reset()
			event_from_private.reset()
			event_from_group.reset()
			event_from_channel.reset()
			event_from_user_action.reset()
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
			import cc.sukazyo.cono.morny.system.telegram_api.formatting.TelegramFormatter.ChatTypeTag.*
			import cc.sukazyo.cono.morny.util.UseMath.percentageOf as p
			val processed = runningTime.count
			val canceled = eventCanceled
			val ignored = eventTotal - processed - canceled
			// language=html
			s""" - <i>total event received</i>: <code>$eventTotal</code>
			   |   - <i>from</i> <code>${event_from_channel.count}</code> <i>$CHANNEL channels</i>
			   |   - <i>from</i> <code>${event_from_group.count}</code> <i>$SUPERGROUP groups/supergroups</i>
			   |   - <i>from</i> <code>${event_from_private.count}</code> <i>$PRIVATE private chats</i>
			   |   - <i>from</i> <code>${event_from_user_action.count}</code> <i>😼 user actions</i>
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
				event.update.sourceChat match
					case None =>
						event.update.sourceUser match
							case None =>
							case Some(user) =>
								event_from_user_action << hashId(user.id).toHex
					case Some(chat) =>
						chat.`type` match
							case Chat.Type.Private =>
								event_from_private << hashId(chat.id).toHex
							case Chat.Type.group | Chat.Type.supergroup =>
								event_from_group << hashId(chat.id).toHex
							case Chat.Type.channel =>
								event_from_channel << hashId(chat.id).toHex
				event.state match
					case State.OK(from) =>
						val timeUsed = EventTimeUsed(System.currentTimeMillis - event.timeStartup)
						givenCxt << timeUsed
						logger `debug`
							s"""event done with OK
							   |  with time consumed ${timeUsed.it}ms
							   |  by $from""".stripMargin
						runningTime ++ timeUsed.it
					case State.CANCELED(from) =>
						eventCanceled += 1
						logger `debug`
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

object MornyReport {
	
	def inCoeur (coeur: MornyCoeur): MornyReport =
		coeur.externalContext.getUnsafe[MornyReport]
	
}
