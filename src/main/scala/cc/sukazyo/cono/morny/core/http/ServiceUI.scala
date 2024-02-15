package cc.sukazyo.cono.morny.core.http

import cats.effect.IO
import cc.sukazyo.cono.morny.core.http.api.HttpService4Api
import cc.sukazyo.cono.morny.data.TelegramImages
import org.http4s.{HttpRoutes, MediaType}
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`

class ServiceUI extends HttpService4Api {
	
	override lazy val service: HttpRoutes[IO] = HttpRoutes.of[IO] {
		case GET -> Root =>
			NotImplemented(TelegramImages.IMG_501.get)
				.map(_.withContentType(`Content-Type`(MediaType.image.jpeg)))
	}
	
}
