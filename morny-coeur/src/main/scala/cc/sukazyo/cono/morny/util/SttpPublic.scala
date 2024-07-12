package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.core.MornySystem
import sttp.client3.{basicRequest, RequestT}
import sttp.client3
import sttp.model.Header

object SttpPublic {
	
	object Schemes {
		val HTTP = "http"
		val HTTPS = "https"
	}
	
	object Headers {
		
		object UserAgent {
			
			private val key = "User-Agent"
			
			/** The default Morny User-Agent header.
			  *
			  * It follows the following template:
			  *  `MornyCoeur / <morny-version>`
			  *
			  * @since 2.0.0
			  */
			val MORNY_CURRENT: Header = Header(key, s"MornyCoeur / ${MornySystem.VERSION}")
			
		}
		
	}
	
	/** The basic request template for Morny's HTTP requests.
	  *
	  * It is a expansion of [[sttp.client3.basicRequest]] with the following features:
	  *  - A [[Headers.UserAgent.MORNY_CURRENT]] User-Agent header.
	  *
	  * @since 2.0.0
	  */
	val mornyBasicRequest: RequestT[client3.Empty, Either[String, String], Any] =
		basicRequest
			.header(Headers.UserAgent.MORNY_CURRENT, true)
	
}
