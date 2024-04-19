package cc.sukazyo.cono.morny.core.http.services

import cats.effect.IO
import cc.sukazyo.cono.morny.core.http.api.HttpService4Api
import cc.sukazyo.cono.morny.core.MornySystem
import cc.sukazyo.cono.morny.util.CommonFormat
import org.http4s.{HttpRoutes, Response}
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.io.*

class Ping extends HttpService4Api {
	
	private case class PingResult (
		pong: Boolean = true,
		time: String = CommonFormat.formatDate(System.currentTimeMillis(), 0),
		server: String = MornySystem.VERSION_FULL,
	)
	
	override lazy val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
		case GET -> Root / "ping" =>
			import io.circe.generic.auto.*
			import io.circe.syntax.*
			Ok(PingResult().asJson)
	}
	
}
