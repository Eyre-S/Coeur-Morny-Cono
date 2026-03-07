package cc.sukazyo.cono.morny.system.telegram_api.account

import cc.sukazyo.cono.morny.system.telegram_api.TelegramExtensions.Bot.exec
import cc.sukazyo.cono.morny.system.telegram_api.action.ClientRequestException
import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.request.BaseRequest
import com.pengrad.telegrambot.response.BaseResponse

trait AbstractBotAccount {
	
	/** Get the [[TelegramBot]] instance associated to this account.
	  *
	  * For a simple usage, it will always return a fixed [[TelegramBot]] instance. But for
	  * account implementations that has load-balance features, it may return different
	  * [[TelegramBot]] for different calls.
	  */
	def getTelegramBot: TelegramBot
	
	/** Try sync execute a [[BaseRequest request]], and throws [[ClientRequestException]]
	  * when it fails.
	  *
	  * It will use [[Telegram.execute]] to execute the request, and check if the response is failed.
	  *
	  * If the request returned a [[BaseResponse response]], the [[BaseResponse.isOk]] will be checked.
	  * if it is false, the method will ended with a [[ClientRequestException.ActionFailed]], which
	  * message is param [[onError_message]](or [[BaseResponse.errorCode]] if it is empty).
	  *
	  * If the request failed in the client (that means [[TelegramBot.execute]] call failed with Exceptions),
	  * this method will ended with a [[ClientRequestException.ClientFailed]].
	  *
	  * @param request         The request needed to be run.
	  * @param onError_message The exception message that will be thrown when the [[request]]'s
	  *                        [[BaseResponse response]] is not ok([[BaseResponse.isOk isOk()]] == false)
	  * @tparam T              Type of the request
	  * @tparam R Type of the response that request should returns.
	  *
	  * @throws ClientRequestException Whenever the request's response is not ok, or the request does not
	  * return a response. See above for more info.
	  * @return The succeed response (which is returned by [[TelegramBot.execute]]) as is.
	  *
	  * @since 1.0.0
	  */
	@throws[ClientRequestException]
	def exec[T <: BaseRequest[T, R], R <: BaseResponse] (request: BaseRequest[T, R], onError_message: String = ""): R =
		getTelegramBot.exec(request, onError_message)
	
}
