package cc.sukazyo.cono.morny.system.telegram_api.formatting

import cc.sukazyo.cono.morny.system.utils.CommonEncrypt
import cc.sukazyo.cono.morny.system.utils.ConvertByteHex.toHex

object NamingUtils {
	
	def inlineQueryId (tag: String, taggedData: String = ""): String =
		CommonEncrypt.MD5(tag+taggedData) toHex
	
}
