package cc.sukazyo.cono.morny.daemon

import cc.sukazyo.cono.morny.{MornyCoeur, MornyConfig}
import cc.sukazyo.cono.morny.Log.{exceptionLog, logger}
import cc.sukazyo.cono.morny.bot.command.MornyInformation
import cc.sukazyo.cono.morny.util.tgapi.event.EventRuntimeException
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramFormatter.*
import cc.sukazyo.cono.morny.util.tgapi.formatting.TelegramParseEscape.escapeHtml as h
import com.google.gson.GsonBuilder
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.model.User
import com.pengrad.telegrambot.request.{BaseRequest, SendMessage}
import com.pengrad.telegrambot.response.BaseResponse

object MornyReport {
	
	private def unsupported: Boolean = (!MornyCoeur.available) || (MornyCoeur.config.reportToChat == -1)
	
	private def executeReport[T <: BaseRequest[T, R], R<: BaseResponse] (report: T): Unit = {
		if unsupported then return
		try {
			MornyCoeur.extra exec report
		} catch case e: EventRuntimeException.ActionFailed => {
			logger warn
					s"""cannot execute report to telegram:
					   |${exceptionLog(e) indent 4}
					   |  tg-api response:
					   |${(e.response toString) indent 4}"""
					.stripMargin
		}
	}
	
	def exception (e: Throwable, description: String|Null = null): Unit = {
		if unsupported then return
		def _tgErrFormat: String = e match
			case api: EventRuntimeException.ActionFailed =>
				// language=html
				"\n\ntg-api error:\n<pre><code>%s</code></pre>"
						.formatted(GsonBuilder().setPrettyPrinting().create.toJson(api.response))
			case _ => ""
		executeReport(SendMessage(
			MornyCoeur.config.reportToChat,
			// language=html
			s"""<b>▌Coeur Unexpected Exception </b>
			   |${if description ne null then h(description)+"\n" else ""}
			   |<pre><code>${h(exceptionLog(e))}</code></pre>$_tgErrFormat"""
			.stripMargin
		).parseMode(ParseMode HTML))
	}
	
	def unauthenticatedAction (action: String, user: User): Unit = {
		if unsupported then return
		executeReport(SendMessage(
			MornyCoeur.config.reportToChat,
			// language=html
			s"""<b>▌User unauthenticated action</b>
			   |action: ${h(action)}
			   |by user ${user.fullnameRefHTML}"""
			.stripMargin
		).parseMode(ParseMode HTML))
	}
	
	def onMornyLogin(): Unit = {
		executeReport(SendMessage(
			MornyCoeur.config.reportToChat,
			// language=html
			s"""<b>▌Morny Logged in</b>
			   |-v ${MornyInformation.getVersionAllFullTagHTML}
			   |as user ${MornyCoeur.username}
			   |
			   |as config fields:
			   |${sectionConfigFields(MornyCoeur.config)}"""
			.stripMargin
		).parseMode(ParseMode HTML))
	}
	
	//noinspection ScalaWeakerAccess
	def sectionConfigFields (config: MornyConfig): String = {
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
	
	def onMornyExit (causedBy: AnyRef|Null): Unit = {
		if unsupported then return
		val causedTag = causedBy match
			case u: User => u.fullnameRefHTML
			case n if n == null => "UNKNOWN reason"
			case a: AnyRef => /*language=html*/ s"<code>${h(a.toString)}</code>"
		executeReport(SendMessage(
			MornyCoeur.config.reportToChat,
			// language=html
			s"""<b>▌Morny Exited</b>
			   |from user @${MornyCoeur.username}
			   |
			   |by: $causedTag"""
			.stripMargin
		).parseMode(ParseMode HTML))
	}
	
}
