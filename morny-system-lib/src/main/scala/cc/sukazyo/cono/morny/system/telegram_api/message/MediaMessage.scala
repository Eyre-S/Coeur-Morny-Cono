package cc.sukazyo.cono.morny.system.telegram_api.message

import cc.sukazyo.cono.morny.system.telegram_api.Natives.NativeSendRequest
import com.pengrad.telegrambot.request.BaseRequest
import com.pengrad.telegrambot.response.BaseResponse

trait MediaMessage [Q <: NativeSendRequest[T, R], T <: BaseRequest[T, R], R <: BaseResponse]
	extends Message with SendableMessage[Q, T, R]
