package cc.sukazyo.cono.morny.core.http.api

import cats.effect.IO
import cc.sukazyo.cono.morny.util.UseThrowable.toLogString
import org.http4s.{HttpRoutes, Response}

trait HttpService4Api {
	
	lazy val service: HttpRoutes[IO]
	
	extension (response: Response[IO]) {
		def setMornyInternalErrorHeader (e: Throwable): Response[IO] =
			response.setMornyInternalErrorHeader(
				e.getClass.getSimpleName,
				e.getMessage,
				e.toLogString
			)
		def setMornyInternalErrorHeader (
			`Morny-Internal-Error-Type`: String,
			`Morny-Internal-Error-Message`: String,
			`Morny-Internal-Error-Detail`: String
		): Response[IO] =
			response.withHeaders(
				"Morny-Internal-Error-Type" -> `Morny-Internal-Error-Type`,
				"Morny-Internal-Error-Message" -> `Morny-Internal-Error-Message`,
				"Morny-Internal-Error-Detail" -> `Morny-Internal-Error-Detail`,
			)
	}
	
}

object HttpService4Api {
	def apply (_service: HttpRoutes[IO]): HttpService4Api = new HttpService4Api:
		override lazy val service: HttpRoutes[IO] = _service
}
