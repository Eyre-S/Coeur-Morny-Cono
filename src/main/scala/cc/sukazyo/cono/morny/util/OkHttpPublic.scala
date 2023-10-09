package cc.sukazyo.cono.morny.util

import okhttp3.MediaType

/** some public values of [[okhttp3]] */
object OkHttpPublic {
	
	/** predefined [[okhttp3]] [[MediaType]]s */
	object MediaTypes:
		/** [[MediaType]] of [[https://en.wikipedia.org/wiki/JSON JSON]]. using encoding ''UTF-8'' */
		val JSON: MediaType = MediaType.get("application/json; charset=utf-8")
	
}
