package cc.sukazyo.cono.morny.core.http.internal

import cc.sukazyo.cono.morny.core.MornyCoeur
import cc.sukazyo.cono.morny.core.http.api.{HttpServer, HttpService4Api, MornyHttpServerContext}
import cc.sukazyo.cono.morny.core.http.ServiceUI
import cc.sukazyo.cono.morny.core.Log.{exceptionLog, logger}

import scala.collection.mutable

class MornyHttpServerContextImpl (using coeur: MornyCoeur) extends MornyHttpServerContext {
	
	private val services_api = mutable.Queue.empty[HttpService4Api]
	
	private lazy val service_ui = ServiceUI()
	
	override def register4API (services: HttpService4Api*): Unit =
		services_api ++= services
	
	override def start: HttpServer = {
		import cats.data.OptionT
		import cats.effect.unsafe.implicits.global
		import cats.effect.IO
		import cats.implicits.toSemigroupKOps
		import org.http4s.server.{Router, Server}
		import org.http4s.server.middleware.{ErrorAction, ErrorHandling}
		
		val router = Router(
			"/" -> service_ui.service,
			"/api" -> services_api.map(_.service).reduce(_ <+> _)
		)
		
		def errorHandler (t: Throwable, message: =>String): OptionT[IO, Unit] =
			OptionT.liftF(IO {
				logger error
					s"""Unexpected exception occurred on Morny Http Server :
					   |${exceptionLog(t)}""".stripMargin
			})
		val withErrorHandler = ErrorHandling.Recover.total(
			ErrorAction.log(
				router,
				messageFailureLogAction = errorHandler,
				serviceErrorLogAction = errorHandler
			)
		)
		
		val server = org.http4s.netty.server.NettyServerBuilder[IO]
			.bindHttp(coeur.config.httpPort, "0.0.0.0")
			.withHttpApp(withErrorHandler.orNotFound)
			.resource
		val (_server, _shutdown_io) = server.allocated.unsafeRunSync() match
			case (_1, _2) => (_1, _2)
		logger notice s"Morny HTTP Server started at ${_server.baseUri}"
		
		new HttpServer(using global):
			val server: Server = _server
			private val shutdown_io = _shutdown_io
			override def stop (): Unit = shutdown_io.unsafeRunSync()
		
	}
	
}
