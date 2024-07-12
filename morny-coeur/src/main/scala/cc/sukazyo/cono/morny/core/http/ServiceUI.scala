package cc.sukazyo.cono.morny.core.http

import cats.effect.IO
import cc.sukazyo.cono.morny.core.http.api.HttpService4Api
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*

class ServiceUI extends HttpService4Api {
	
	override lazy val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
		case GET -> Root =>
			MornyNotImplemented()
	}
	
}
