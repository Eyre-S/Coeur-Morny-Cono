package cc.sukazyo.cono.morny.util.tgapi.formatting

import cc.sukazyo.cono.morny.util.CommonEncrypt
import cc.sukazyo.cono.morny.util.ConvertByteHex.toHex

object NamingUtils {
	
	def inlineQueryId (tag: String, taggedData: String = ""): String =
		CommonEncrypt.MD5(tag+taggedData) toHex
	
}
