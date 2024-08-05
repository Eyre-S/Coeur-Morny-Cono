package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.MornySystem
import sttp.client3.{basicRequest, RequestT}
import sttp.model.Header

object SttpPublic {
	
	object Schemes {
		val HTTP = "http"
		val HTTPS = "https"
	}
	
	object Headers {
		
		object UserAgent {
			
			private val key = "User-Agent"
			
			val MORNY_CURRENT: Header = Header(key, s"MornyCoeur / ${MornySystem.VERSION}")
			
		}
		
	}
	
	val mornyBasicRequest: RequestT[sttp.client3.Empty, Either[String, String], Any] =
		basicRequest
			.header(Headers.UserAgent.MORNY_CURRENT, true)
	
}
