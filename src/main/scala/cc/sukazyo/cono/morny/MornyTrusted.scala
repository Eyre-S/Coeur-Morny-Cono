package cc.sukazyo.cono.morny

import com.pengrad.telegrambot.model.ChatMember.Status

class MornyTrusted (using coeur: MornyCoeur)(using config: MornyConfig) {
	
	def isTrusted (userId: Long): Boolean =
		if userId == config.trustedMaster then true
		else if config.trustedChat == -1 then false
		else coeur.extra isUserInGroup(userId, config.trustedChat, Status.administrator)
	
	def isTrusted_dinnerReader (userId: Long): Boolean =
		if userId == config.trustedMaster then true
		else config.dinnerTrustedReaders contains userId
	
}
