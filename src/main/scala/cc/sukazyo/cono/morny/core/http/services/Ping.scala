package cc.sukazyo.cono.morny.core.http.services

import cats.effect.IO
import cc.sukazyo.cono.morny.core.http.api.HttpService4Api
import org.http4s.{HttpRoutes, Response}
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.io.*

class Ping extends HttpService4Api {
	
	case class PingResult (
		pong: Boolean = true
	)
	
	override lazy val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
		case GET -> Root / "ping" =>
			import io.circe.generic.auto.*
			import io.circe.syntax.*
			Ok(PingResult().asJson)
	}
	
}
