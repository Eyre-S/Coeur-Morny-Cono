package cc.sukazyo.cono.morny.core.http.api

import cats.effect.IO
import cc.sukazyo.cono.morny.data.TelegramImages
import cc.sukazyo.cono.morny.util.StringEnsure.firstLine
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import org.http4s.{Header, MediaType, Response}
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object HttpStatus extends HttpStatus

trait HttpStatus {
	
	private type ResponseT = Response[IO]
	private type ResponseIO = IO[ResponseT]
	
	extension (response: ResponseT) {
		
		
		def setMornyInternalErrorHeader (e: Throwable): ResponseT =
			response.setMornyInternalErrorHeader(
				e.getClass.getSimpleName.firstLine,
				e.getMessage.firstLine,
				URLEncoder.encode(e.toLogString, StandardCharsets.UTF_8) // todo: maybe extract this const
			)
		
		/** Set HTTP Header that shows Morny's Internal Server Error related information.
		  *
		  * It will set the following headers, corresponding to this method's parameters, you
		  * can customize those values.
		  *   - Morny-Internal-Error-Type
		  *   - Morny-Internal-Error-Message
		  *   - Morny-Internal-Error-Detail
		  *
		  * **Notice that ANY header value MUST NOT contains line breaking!** Or it will only
		  * returns an empty 500 response.
		  *
		  * @return the [[ResponseT]], with above headers added.
		  */
		def setMornyInternalErrorHeader (
			`Morny-Internal-Error-Type`: String,
			`Morny-Internal-Error-Message`: String,
			`Morny-Internal-Error-Detail`: String
		): ResponseT =
			response
				.putHeaders(
					"Morny-Internal-Error-Type" -> `Morny-Internal-Error-Type`,
					"Morny-Internal-Error-Message" -> `Morny-Internal-Error-Message`,
					"Morny-Internal-Error-Detail" -> `Morny-Internal-Error-Detail`,
				)
	}
	
	/** 400 Bad Request */
	def MornyBadRequest (): ResponseIO =
		BadRequest(TelegramImages.IMG_400.get)
			.map(_.withContentType(`Content-Type`(MediaType.image.png)))
	
	/** 404 Not Found */
	def MornyNotFound (): ResponseIO =
		NotFound(TelegramImages.IMG_404.get)
			.map(_.withContentType(`Content-Type`(MediaType.image.png)))
	
	/** 500 Internal Server Error */
	def MornyInternalServerError (): ResponseIO =
		InternalServerError(TelegramImages.IMG_500.get)
			.map(_.withContentType(`Content-Type`(MediaType.image.png)))
	
	/** 501 Not Implemented */
	def MornyNotImplemented (): ResponseIO =
		NotImplemented(TelegramImages.IMG_501.get)
			.map(_.withContentType(`Content-Type`(MediaType.image.png)))
	
	/** 523 Service Unavailable */
	def MornyServiceUnavailable (): ResponseIO =
		ServiceUnavailable(TelegramImages.IMG_523.get)
			.map(_.withContentType(`Content-Type`(MediaType.image.png)))
	
}
