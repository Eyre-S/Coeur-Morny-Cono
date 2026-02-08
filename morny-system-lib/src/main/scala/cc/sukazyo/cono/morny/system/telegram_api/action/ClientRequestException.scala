package cc.sukazyo.cono.morny.system.telegram_api.action

import com.pengrad.telegrambot.response.BaseResponse

/** All possible exception when do Telegram Request.
  *
  * Contains following detailed exceptions:
  *  - [[ClientRequestException.ClientFailed]]
  *  - [[ClientRequestException.ActionFailed]]
  */
abstract class ClientRequestException (message: String) extends RuntimeException(message)

object ClientRequestException {
	/** Telegram API request failed due to the response code is not 200 OK.
	  * @param response Raw API response object.
	  */
	class ActionFailed (message: String, val response: BaseResponse) extends ClientRequestException(message)
	/** Client exception occurred when sending request.
	  *
	  * It may be some network exception, or parsing API response exception.
	  *
	  * The client exception is stored in [[getCause]].
	  */
	class ClientFailed (caused: Exception) extends ClientRequestException("API client failed.") {
		this.initCause(caused)
	}
}
