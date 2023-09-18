package cc.sukazyo.cono.morny.util.tgapi.event

import com.pengrad.telegrambot.response.BaseResponse

class EventRuntimeException (message: String) extends RuntimeException(message)

object EventRuntimeException {
	class ActionFailed (message: String, val response: BaseResponse) extends EventRuntimeException(message)
}
