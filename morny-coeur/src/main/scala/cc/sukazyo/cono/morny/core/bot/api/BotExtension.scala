package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.messages.ErrorMessage
import cc.sukazyo.cono.morny.util.hytrans.LangTag
import com.pengrad.telegrambot.model.User

object BotExtension extends BotExtension

/** Bot extensions for Morny feature. */
trait BotExtension {
	
	extension (errorMessage: ErrorMessage[?, ?]) {
		
		/** Submit this [[ErrorMessage]] to a [[MornyCoeur]].
		  * 
		  * Will send this [[ErrorMessage]] with the basic send config.
		  *
		  * @see [[cc.sukazyo.cono.morny.core.bot.internal.ErrorMessageManager.sendErrorMessage]]
		  * @since 2.0.0
		  */
		def submit (using coeur: MornyCoeur): Unit =
			coeur.errorMessageManager.sendErrorMessage(errorMessage)
		
	}
	
	extension (user: User) {
		
		/** Get this telegram [[User]]'s prefer language.
		  * 
		  * It will check the [[User.languageCode]] provided by Telegram API.
		  * 
		  * If a language code is provided, it will be [[LangTag.normalizeLangTag normalized]]
		  * to [[LangTag]] and return.
		  * 
		  * If cannot find a language code associated with this [[User]], the empty string will
		  * be return.
		  * 
		  * @return A [[LangTag.normalizeLangTag normalized]] [[LangTag]] that should be this
		  *         [[User]]'s prefer language, or a empty string.
		  */
		def prefer_language: String =
			user.languageCode match
				case null => ""
				case x => LangTag.normalizeLangTag(x)
		
	}
	
}
