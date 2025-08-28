package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.MornySystem
import sttp.client3
import sttp.client3.{RequestT, basicRequest}
import sttp.model.{Header, HeaderNames}

object SttpPublic {
	
	object Schemes {
		val HTTP = "http"
		val HTTPS = "https"
	}
	
	object Headers {
		
		object UserAgent {
			
			val MORNY_CURRENT: Header = Header(HeaderNames.UserAgent, s"MornyCoeur / ${MornySystem.VERSION}")
			
		}
		
	}
	
	val mornyBasicRequest: RequestT[client3.Empty, Either[String, String], Any] =
		basicRequest
			.header(Headers.UserAgent.MORNY_CURRENT, true)
	
}
