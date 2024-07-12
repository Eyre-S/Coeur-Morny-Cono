package cc.sukazyo.cono.morny.morny_misc

import cc.sukazyo.cono.morny.system.utils.EpochDateTime.{EpochDays, EpochMillis}
import com.pengrad.telegrambot.model.User

import scala.language.postfixOps

object MornyJrrp {
	
	def jrrp_of_telegramUser (user: User, timestamp: EpochMillis): Double =
		jrrp_v_xmomi(user.id, EpochDays fromMillis timestamp) * 100.0
	
	private def jrrp_v_xmomi (identifier: Long, dayStamp: EpochDays): Double =
		import cc.sukazyo.cono.morny.system.utils.CommonEncrypt.MD5
		import cc.sukazyo.cono.morny.system.utils.ConvertByteHex.toHex
		java.lang.Long.parseLong(MD5(s"$identifier@$dayStamp").toHex.substring(0, 4), 16) / (0xffff toDouble)
	
}
