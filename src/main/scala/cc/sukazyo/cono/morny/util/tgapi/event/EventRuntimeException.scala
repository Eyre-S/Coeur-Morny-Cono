package cc.sukazyo.cono.morny.util.tgapi.event

import com.pengrad.telegrambot.response.BaseResponse

/** All possible exception when do Telegram Request.
  *
  * Contains following detailed exceptions:
  *  - [[EventRuntimeException.ClientFailed]]
  *  - [[EventRuntimeException.ActionFailed]]
  */
abstract class EventRuntimeException (message: String) extends RuntimeException(message)

object EventRuntimeException {
	/** Telegram API request failed due to the response code is not 200 OK.
	  * @param response Raw API response object.
	  */
	class ActionFailed (message: String, val response: BaseResponse) extends EventRuntimeException(message)
	/** Client exception occurred when sending request.
	  *
	  * It may be some network exception, or parsing API response exception.
	  *
	  * The client exception is stored in [[getCause]].
	  */
	class ClientFailed (caused: Exception) extends EventRuntimeException("API client failed.") {
		this.initCause(caused)
	}
}
