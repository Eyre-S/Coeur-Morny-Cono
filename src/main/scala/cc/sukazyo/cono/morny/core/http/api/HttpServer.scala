package cc.sukazyo.cono.morny.core.http.api

import cats.effect.unsafe.IORuntime

trait HttpServer (using IORuntime) {
	
	def stop(): Unit
	
}
