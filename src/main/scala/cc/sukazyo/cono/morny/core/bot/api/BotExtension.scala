package cc.sukazyo.cono.morny.core.bot.api

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.bot.api.messages.ErrorMessage

/** Bot extensions for Morny feature.
  */
object BotExtension {
	
	extension (errorMessage: ErrorMessage[?, ?]) {
		
		/** Submit this [[ErrorMessage]] to a [[MornyCoeur]].
		  * 
		  * Will send this [[ErrorMessage]] with the basic send config.
		  * 
		  * @see [[cc.sukazyo.cono.morny.core.bot.internal.ErrorMessageManager.sendErrorMessage]]
		  */
		def submit (using coeur: MornyCoeur): Unit =
			coeur.errorMessageManager.sendErrorMessage(errorMessage)
		
	}
	
}
