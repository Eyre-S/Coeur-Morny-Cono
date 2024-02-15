package cc.sukazyo.cono.morny.stickers_get.http

import cats.effect.IO
import cc.sukazyo.cono.morny.core.http.api.HttpService4Api
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.data.TelegramImages
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.File.getContent
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.execute
import com.pengrad.telegrambot.request.GetFile
import com.pengrad.telegrambot.TelegramBot
import org.http4s.{HttpRoutes, MediaType}
import org.http4s.dsl.io.*
import org.http4s.headers.`Content-Type`

import java.io.IOException

class StickerService (using coeur: MornyCoeur) extends HttpService4Api {
	
	override lazy val service: HttpRoutes[IO] = HttpRoutes.of {
		
		case GET -> Root / "sticker" / "id" / id =>
			try {
				val response = GetFile(id).execute(using coeur.account)
				if response.isOk then
					try {
						given TelegramBot = coeur.account
						val file = response.file.getContent
						Ok(file)
					} catch {
						case e: IOException =>
							ServiceUnavailable(
								TelegramImages.IMG_523.get,
								`Content-Type`(MediaType.image.png),
							).map(_.setMornyInternalErrorHeader(e))
					}
				else
					NotFound(
						TelegramImages.IMG_404.get,
						`Content-Type`(MediaType.image.png),
					).map(_.setMornyInternalErrorHeader(
						"_telegram_api",
						response.errorCode.toString,
						response.description,
					))
			} catch
				case io: IOException =>
					ServiceUnavailable(
						TelegramImages.IMG_523.get,
						`Content-Type`(MediaType.image.png),
					).map(_.setMornyInternalErrorHeader(io))
				case e: Throwable =>
					InternalServerError(
						TelegramImages.IMG_500.get,
						`Content-Type`(MediaType.image.png),
					).map(_.setMornyInternalErrorHeader(e))
		
		case GET -> Root / "sticker" =>
			NotFound("not found")
		
	}
}
