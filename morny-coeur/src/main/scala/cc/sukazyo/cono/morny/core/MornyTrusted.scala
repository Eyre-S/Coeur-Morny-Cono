package cc.sukazyo.cono.morny.core

import cc.sukazyo.cono.morny.core.Log.logger
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.{LimboChat, LimboUser}
import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Chat.*
import com.pengrad.telegrambot.model.ChatMember.Status
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.model.User

class MornyTrusted (using coeur: MornyCoeur)(using config: MornyConfig) {
	
	if config.trustedMaster == -1 then
		logger `warn` "You have not set your Morny's master.\n  it may have some issues on controlling your bot."
	
	/** If the user can be trusted.
	  * @since 2.0.0
	  * @param user The user that will be check if it is in this trust list. Only user's id will be used,
	  *             so if you don't have a user instance, you can safely use [[LimboUser]].
	  * @return `true` if this user can be trusted, `false` otherwise.
	  */
	infix def isTrust (user: User): Boolean =
		given TelegramBot = coeur.account
		if user.id == config.trustedMaster then true
		else if config.trustedChat == -1 then false
		else LimboChat(config.trustedChat).memberHasPermission(user, Status.administrator)
	
	/** If this user can be trusted to read the dinner messages.
	  * @since 2.0.0
	  * @param user The user that will be check if it is in this trust list. Only user's id will be used,
	  *             so if you don't have a user instance, you can safely use [[LimboUser]].
	  * @return `true` if this user can read dinners, `false` otherwise.
	  */
	infix def isTrust4dinner (user: User): Boolean =
		if user.id == config.trustedMaster then true
		else config.dinnerTrustedReaders `contains` user.id
	
}
