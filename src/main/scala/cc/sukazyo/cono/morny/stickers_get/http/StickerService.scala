package cc.sukazyo.cono.morny.stickers_get.http

import cats.effect.IO
import cc.sukazyo.cono.morny.core.http.api.HttpService4Api
import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.File.getContent
import cc.sukazyo.cono.morny.util.tgapi.TelegramExtensions.Requests.execute
import com.pengrad.telegrambot.request.GetFile
import com.pengrad.telegrambot.TelegramBot
import org.http4s.HttpRoutes
import org.http4s.dsl.io.*

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
							MornyServiceUnavailable()
								.map(_.setMornyInternalErrorHeader(e))
					}
				else
					MornyNotFound()
						.map(_.setMornyInternalErrorHeader(
							"_telegram_api",
							response.errorCode.toString,
							response.description,
						))
			} catch
				case io: IOException =>
					MornyServiceUnavailable()
						.map(_.setMornyInternalErrorHeader(io))
				case e: Throwable =>
					MornyInternalServerError()
						.map(_.setMornyInternalErrorHeader(e))
		
		case GET -> "sticker" /: rest =>
			MornyBadRequest() // TODO: bad request
		
	}
}
