package cc.sukazyo.cono.morny.system.telegram_api.command.name

import cc.sukazyo.cono.morny.system.utils.ConvertByteHex.toHex

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

class EncryptedName (token: String) extends CommandName {
	
	override def isMatch (input: String): Boolean =
		EncryptedName.digest(input) == token
	
	override def isListed: Boolean = false
	
}

object EncryptedName {
	
	private val DIGEST_INSTANCE: MessageDigest = MessageDigest.getInstance("SHA-256")
	
	def digest (in: String): String = {
		val bytes = in.getBytes(StandardCharsets.UTF_8)
		val hashedBytes = Iterator.iterate(bytes)(DIGEST_INSTANCE.digest)
			.drop(2000).next()
		hashedBytes.toHex
	}
	
}
