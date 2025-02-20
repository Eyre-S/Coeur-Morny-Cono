package cc.sukazyo.cono.morny.reporter.telegram_bot

import cc.sukazyo.cono.morny.core.event.TelegramBotEvents
import cc.sukazyo.cono.morny.reporter.MornyReport

class BotErrorsReport (using reporter: MornyReport) {
	
	private val _TBotEvents = TelegramBotEvents.inCoeur(using reporter.coeur)
	
	val onGetUpdateFailed: _TBotEvents.OnGetUpdateFailed.MyCallback
	= telegramException => {
		
		if (telegramException.response != null) {
			// if the response exists, means connections to the server is successful, but
			//  server can't process the request.
			// due to connections to the server is ok, the report should be able to send to
			//  the telegram side.
			reporter.exception(telegramException, "Failed get updates.")
		}
		
		if (telegramException.getCause != null) {
			import java.net.{SocketException, SocketTimeoutException}
			import javax.net.ssl.SSLHandshakeException
			val caused = telegramException.getCause
			caused match
				case _: (SSLHandshakeException|SocketException|SocketTimeoutException) =>
				case e_other =>
					reporter.exception(e_other, "Failed get updates.")
		}
		
	}
	
	val onEventListenersThrowException: _TBotEvents.OnListenerOccursException.MyCallback
	= (e, _, _) => {
		reporter.exception(e, "on event running")
	}
	
}
