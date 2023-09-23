package cc.sukazyo.cono.morny

import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.{LimboChat, LimboUser}
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Chat.*
import com.pengrad.telegrambot.model.ChatMember.Status
import com.pengrad.telegrambot.TelegramBot

class MornyTrusted (using coeur: MornyCoeur)(using config: MornyConfig) {
	
	def isTrusted (userId: Long): Boolean =
		given TelegramBot = coeur.account
		if userId == config.trustedMaster then true
		else if config.trustedChat == -1 then false
		else LimboChat(config.trustedChat) memberHasPermission(LimboUser(userId), Status.administrator)
	
	def isTrusted_dinnerReader (userId: Long): Boolean =
		if userId == config.trustedMaster then true
		else config.dinnerTrustedReaders contains userId
	
}
