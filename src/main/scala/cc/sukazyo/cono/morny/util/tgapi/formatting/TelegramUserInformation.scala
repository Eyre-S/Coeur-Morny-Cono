package cc.sukazyo.cono.morny.util.tgapi.formatting

import com.pengrad.telegrambot.model.User
import sttp.client3.{asString, basicRequest, HttpError, SttpClientException, UriContext}
import sttp.client3.okhttp.OkHttpSyncBackend

import java.io.IOException
import scala.util.matching.Regex

object TelegramUserInformation {
	
	private val DC_QUERY_PROCESSOR_REGEX: Regex = "(cdn[1-9]).tele(sco.pe|gram-cdn.org)"r
	
	private val httpClient = OkHttpSyncBackend()
	
	@throws[IllegalArgumentException|IOException]
	def getDataCenterFromUser (username: String): String = {
		
		try
			val body = basicRequest
				.get(uri"https://t.me/$username")
				.response(asString.getRight)
				.send(httpClient)
				.body
			DC_QUERY_PROCESSOR_REGEX.findFirstMatchIn(body) match
				case Some(res) => res.group(1)
				case None => "<no-cdn-information>"
		catch
			case _: SttpClientException =>
				"<error-http-request>"
			case _: HttpError[_] =>
				"<error-parse-response>"
	}
	
	def getFormattedInformation (user: User): String = {
		import TelegramParseEscape.escapeHtml as h
		
		val userInfo = StringBuilder()
		
		userInfo ++= // language=html
				s"""userid :
				   |- <code>${user.id}</code>"""
				.stripMargin
		userInfo ++= {
			if (user.username eq null) // language=html
				s"""
				   |username : <u>null</u>
				   |datacenter : <u>not supported</u>"""
						.stripMargin
			else // language=html
				s"""
				   |username :
				   |- <code>${h(user.username)}</code>
				   |datacenter :
				   |- <code>${h(getDataCenterFromUser(user.username))}</code>"""
				.stripMargin
		}
		userInfo ++= // language=html
				s"""
				   |display name :
				   |- <code>${h(user.firstName)}</code>${if user.lastName ne null then s"\n- <code>${h(user.lastName)}</code>" else ""}"""
				.stripMargin
		if (user.languageCode ne null) userInfo ++= // language=html
				s"""
				   |language-code :
				   |- <code>${user.languageCode}</code>"""
				.stripMargin
		
		userInfo toString
		
	}
	
}
