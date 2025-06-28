package cc.sukazyo.cono.morny.util.tgapi.event

import cc.sukazyo.cono.morny.bot.api.EventEnv
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
	
	/**
	  * When the exception is thrown by an event listener.
	  *
	  * This includes the context of the event, and event runner's states information.
	  * 
	  * The original [[Exception]] that thrown by the listener is stored in [[getCause]]. The caused can also be
	  * [[ActionFailed]] or [[ClientFailed]].
	  *
	  * @param ex The original [[Exception]] that thrown by the listener. Will be stored in [[getCause]].
	  * @param currentListener Current listener that processing this event and throws the [[getCause]] original
	  *                        exception.
	  * @param currentSubevent In which subevent that listener is processing.
	  * @param context The context of the listener's processing event.
	  */
	class EventListenerFailed (ex: Throwable)(
		val currentListener: String, val currentSubevent: String,
		val context: EventEnv
	) extends EventRuntimeException(s"Event failed when listener $currentListener is processing $currentSubevent.") {
		this.initCause(ex)
	}
	
}
