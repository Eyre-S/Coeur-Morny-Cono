package cc.sukazyo.cono.morny.core.http.api

import cats.effect.IO
import cc.sukazyo.cono.morny.data.TelegramImages
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import org.http4s.{Header, MediaType, Response}
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`

trait HttpStatus {
	
	private type ResponseT = Response[IO]
	private type ResponseIO = IO[ResponseT]
	
	extension (response: ResponseT) {
		def setMornyInternalErrorHeader (e: Throwable): ResponseT =
			response.setMornyInternalErrorHeader(
				e.getClass.getSimpleName,
				e.getMessage,
				e.toLogString
			)
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
	def MornyInternalServerError  (): ResponseIO =
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
