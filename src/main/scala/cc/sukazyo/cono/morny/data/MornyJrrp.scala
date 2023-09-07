package cc.sukazyo.cono.morny.data

import com.pengrad.telegrambot.model.User

import scala.language.postfixOps

object MornyJrrp {
	
	def jrrp_of_telegramUser (user: User, timestamp: Long): Double =
		jrrp_v_xmomi(user.id, timestamp/(1000*60*60*24)) * 100.0
	
	private def jrrp_v_xmomi (identifier: Long, dayStamp: Long): Double =
		import cc.sukazyo.cono.morny.util.CommonConvert.byteArrayToHex
		import cc.sukazyo.cono.morny.util.CommonEncrypt.hashMd5
		(java.lang.Long parseLong byteArrayToHex(hashMd5(s"$identifier@$dayStamp")).substring(0, 4)) / (0xffff toDouble)
	
}
