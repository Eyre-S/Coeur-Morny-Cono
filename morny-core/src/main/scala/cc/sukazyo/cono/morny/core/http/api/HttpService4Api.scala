package cc.sukazyo.cono.morny.core.http.api

import cats.effect.IO
import cc.sukazyo.cono.morny.core.MornyCoeur
import org.http4s.HttpRoutes

trait HttpService4Api extends HttpStatus {
	
	lazy val service: HttpRoutes[IO]
	
}

object HttpService4Api {
	def apply (_service: HttpRoutes[IO])(using MornyCoeur): HttpService4Api = new HttpService4Api:
		override lazy val service: HttpRoutes[IO] = _service
}
