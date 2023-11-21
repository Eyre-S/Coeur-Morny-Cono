package cc.sukazyo.cono.morny.util

import cc.sukazyo.cono.morny.MornySystem
import sttp.model.Header

object SttpPublic {
	
	object Schemes {
		val HTTP = "http"
		val HTTPS = "https"
	}
	
	object Headers {
		
		object UserAgent {
			
			private val key = "User-Agent"
			
			val MORNY_CURRENT = Header(key, s"MornyCoeur / ${MornySystem.VERSION}")
			
		}
		
	}
	
}
